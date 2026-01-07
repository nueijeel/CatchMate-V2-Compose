package com.catchmate.presentation.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentBlockedSettingBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.interaction.OnBlockedUserSelectedListener
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.BlockedSettingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedSettingFragment :
    BaseFragment<FragmentBlockedSettingBinding>(FragmentBlockedSettingBinding::inflate),
    OnBlockedUserSelectedListener {
    private lateinit var blockedUserAdapter: BlockedUserListAdapter
    private val blockedSettingViewModel: BlockedSettingViewModel by viewModels()
    private var deletedUserId = -1L
    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var blockedUserList: MutableList<GetUserProfileResponse> = mutableListOf()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
        getBlockedUserList()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderBlockedSetting.tvHeaderTextTitle.text = getString(R.string.mypage_setting_block_setting)
            layoutHeaderBlockedSetting.imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
            blockedUserAdapter = BlockedUserListAdapter(this@BlockedSettingFragment)
            rvBlockedUserListBlockedSetting.apply {
                adapter = blockedUserAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                addOnScrollListener(
                    object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(
                            recyclerView: RecyclerView,
                            dx: Int,
                            dy: Int,
                        ) {
                            super.onScrolled(recyclerView, dx, dy)
                            val lastVisibleItemPosition =
                                (recyclerView.layoutManager as LinearLayoutManager)
                                    .findLastCompletelyVisibleItemPosition()
                            val itemTotalCount = recyclerView.adapter!!.itemCount
                            if (lastVisibleItemPosition + 1 >= itemTotalCount && !isLastPage && !isLoading) {
                                currentPage += 1
                                getBlockedUserList()
                            }
                        }
                    },
                )
            }
        }
    }

    private fun initViewModel() {
        blockedSettingViewModel.getBlockedUserListResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.rvBlockedUserListBlockedSetting.visibility = View.GONE
                binding.layoutBlockedSettingNoList.visibility = View.VISIBLE
            } else {
                binding.rvBlockedUserListBlockedSetting.visibility = View.VISIBLE
                binding.layoutBlockedSettingNoList.visibility = View.GONE
                if (isApiCalled) {
                    blockedUserList.addAll(response.userInfoList)
                }
                blockedUserAdapter.submitList(blockedUserList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }
        blockedSettingViewModel.deleteBlockedUserResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                blockedSettingViewModel.deleteUserFromList(deletedUserId)
            }
        }
        blockedSettingViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.blockedSettingFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_blockedSettingFragment_to_loginFragment, bundle, navOptions)
            }
        }
        blockedSettingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun getBlockedUserList() {
        if (isLoading || isLastPage) return
        isLoading = true
        blockedSettingViewModel.getBlockedUserList(currentPage)
        isApiCalled = true
    }

    override fun onBlockedUserSelected(
        pos: Int,
        userId: Long,
        nickname: String,
    ) {
        deletedUserId = userId
        showBlockDeleteDialog(userId, nickname)
    }

    private fun showBlockDeleteDialog(
        userId: Long,
        nickname: String,
    ) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.apply {
            val title = getString(R.string.mypage_setting_block_dialog_title)
            tvSimpleDialogTitle.text = title.format(nickname)
            tvSimpleDialogNegative.apply {
                setText(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.mypage_setting_block_dialog_pov_btn)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    blockedSettingViewModel.deleteBlockedUser(userId)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}
