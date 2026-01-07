package com.catchmate.presentation.view.mypage

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.board.Board
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentMyPostBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.interaction.OnPostItemClickListener
import com.catchmate.presentation.util.AgeUtils
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.GenderUtils
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.MyPostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPostFragment :
    BaseFragment<FragmentMyPostBinding>(FragmentMyPostBinding::inflate),
    OnPostItemClickListener {
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private val myPostViewModel: MyPostViewModel by viewModels()

    private var userInfo: GetUserProfileResponse? = null
    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var myPostList: MutableList<Board> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getUserInfo()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        getLocalUserId()
        setUserData()
        initHeader()
        initViewModel()
        initRecyclerView()
        getMyPostList()
    }

    private fun getUserInfo(): GetUserProfileResponse? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("userInfo", GetUserProfileResponse::class.java)
        } else {
            arguments?.getParcelable("userInfo") as GetUserProfileResponse?
        }

    private fun getLocalUserId() {
        localDataViewModel.getUserId()
        localDataViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId == userInfo?.userId) {
                binding.layoutHeaderMyPost.imgbtnHeaderKebabMenu.visibility = View.INVISIBLE
            } else {
                binding.layoutHeaderMyPost.imgbtnHeaderKebabMenu.visibility = View.VISIBLE
            }
        }
    }

    private fun setUserData() {
        binding.viewMyPostProfile.binding.apply {
            imgbtnMyPageUserProfileDetail.visibility = View.GONE
            Glide
                .with(this@MyPostFragment)
                .load(userInfo?.profileImageUrl)
                .into(ivMyPageUserProfile)

            tvMyPageUserProfileNickname.text = userInfo?.nickName
            tvMyPageUserProfileTeamBadge.text = ClubUtils.convertClubIdToName(userInfo?.favoriteClub?.id!!)
            DrawableCompat
                .setTint(
                    tvMyPageUserProfileTeamBadge.background,
                    convertTeamColor(
                        requireContext(),
                        userInfo?.favoriteClub?.id!!,
                        true,
                        "mypost",
                    ),
                )

            if (userInfo?.watchStyle.isNullOrEmpty()) {
                tvMyPageUserProfileCheerStyleBadge.visibility = View.GONE
            } else {
                tvMyPageUserProfileCheerStyleBadge.visibility = View.VISIBLE
                tvMyPageUserProfileCheerStyleBadge.text = userInfo?.watchStyle
            }
            tvMyPageUserProfileGenderBadge.text = GenderUtils.convertBoardGender(requireContext(), userInfo?.gender!!)
            tvMyPageUserProfileAgeBadge.text = AgeUtils.convertBirthDateToAge(userInfo?.birthDate!!)
        }
    }

    private fun initHeader() {
        binding.layoutHeaderMyPost.apply {
            imgbtnHeaderKebabMenuBack.setOnClickListener {
                findNavController().popBackStack()
            }
            imgbtnHeaderKebabMenu.setOnClickListener {
                val popup = PopupMenu(requireContext(), imgbtnHeaderKebabMenu, Gravity.CENTER, 0, R.style.CustomPopupMenu)
                popup.menuInflater.inflate(R.menu.menu_my_post_other_user, popup.menu)
                val targetItem = popup.menu.findItem(R.id.menuItem_my_post_report)
                val s = SpannableString(targetItem.title)
                s.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.brand500)), 0, s.length, 0)
                targetItem.title = s

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuItem_my_post_block -> { // 차단
                            showUserBlockDialog()
                            true
                        }

                        R.id.menuItem_my_post_report -> { // 신고
                            val bundle =
                                Bundle().apply {
                                    putString("nickname", userInfo?.nickName!!)
                                    putLong("userId", userInfo?.userId!!)
                                }
                            findNavController().navigate(R.id.action_myPostFragment_to_reportFragment, bundle)
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
                popup.show()
            }
        }
    }

    private fun initViewModel() {
        myPostViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Log.i("MY POST ERR", msg)
            }
        }
        myPostViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.myPostFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_myPostFragment_to_loginFragment, bundle, navOptions)
            }
        }
        myPostViewModel.getUserBoardListResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.rvMyPost.visibility = View.GONE
                binding.layoutMyPostNoList.visibility = View.VISIBLE
            } else {
                binding.rvMyPost.visibility = View.VISIBLE
                binding.layoutMyPostNoList.visibility = View.GONE
                if (isApiCalled) {
                    myPostList.addAll(response.boardInfoList)
                }
                val adapter = binding.rvMyPost.adapter as MyPostAdapter
                adapter.updatePostList(myPostList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }
        myPostViewModel.postUserBlockResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                binding.rvMyPost.visibility = View.GONE
                binding.layoutMyPostBlockedUser.visibility = View.VISIBLE
                Snackbar.make(requireView(), R.string.mypage_mypost_user_block_toast, Snackbar.LENGTH_SHORT).show()
            }
        }
        myPostViewModel.userBlockFailureMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvMyPost.apply {
            adapter = MyPostAdapter(requireContext(), layoutInflater, this@MyPostFragment)
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
                            getMyPostList()
                        }
                    }
                },
            )
        }
    }

    private fun getMyPostList() {
        if (isLoading || isLastPage) return
        isLoading = true
        myPostViewModel.getUserBoardList(userInfo?.userId!!, currentPage)
        isApiCalled = true
    }

    private fun showUserBlockDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.apply {
            val title = getString(R.string.mypage_mypost_user_block_dialog_title)
            tvSimpleDialogTitle.text = title.format(userInfo?.nickName)
            tvSimpleDialogNegative.apply {
                setText(R.string.dialog_button_cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            tvSimpleDialogPositive.apply {
                setText(R.string.mypage_mypost_user_block_dialog_pov)
                setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.brand500),
                )
                setOnClickListener {
                    myPostViewModel.postUserBlock(userInfo?.userId!!)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onPostItemClicked(boardId: Long) {
        val bundle = Bundle()
        bundle.putLong("boardId", boardId)
        findNavController().navigate(R.id.action_myPostFragment_to_readPostFragment, bundle)
    }
}
