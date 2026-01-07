package com.catchmate.presentation.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.enroll.AllReceivedEnrollInfoResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentReceivedJoinBinding
import com.catchmate.presentation.interaction.OnReceivedEnrollClickListener
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.ReceivedJoinViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceivedJoinFragment :
    BaseFragment<FragmentReceivedJoinBinding>(FragmentReceivedJoinBinding::inflate),
    OnReceivedEnrollClickListener {
    private var newCount: Int = -1
    private val receivedJoinViewModel: ReceivedJoinViewModel by viewModels()
    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var receivedJoinList: MutableList<AllReceivedEnrollInfoResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newCount = arguments?.getInt("newCount") ?: -1
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        initViewModel()
        initRecyclerView()
        getAllReceivedEnroll()
    }

    private fun initHeader() {
        binding.layoutHeaderReceivedJoin.apply {
            tvHeaderTextTitle.setText(R.string.mypage_received_join)
            if (newCount > 0) {
                tvHeaderTextUnreadMessageCountBadge.visibility = View.VISIBLE
                tvHeaderTextUnreadMessageCountBadge.text = newCount.toString()
            } else {
                tvHeaderTextUnreadMessageCountBadge.visibility = View.INVISIBLE
            }
            imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvReceivedJoinList.apply {
            adapter = ReceivedJoinAdapter(layoutInflater, this@ReceivedJoinFragment)
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
                            getAllReceivedEnroll()
                        }
                    }
                },
            )
        }
    }

    private fun getAllReceivedEnroll() {
        if (isLoading || isLastPage) return
        isLoading = true
        receivedJoinViewModel.getAllReceivedEnroll(currentPage)
        isApiCalled = true
    }

    private fun initViewModel() {
        receivedJoinViewModel.getAllReceivedEnrollResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.rvReceivedJoinList.visibility = View.GONE
                binding.layoutReceivedJoinNoList.visibility = View.VISIBLE
            } else {
                binding.rvReceivedJoinList.visibility = View.VISIBLE
                binding.layoutReceivedJoinNoList.visibility = View.GONE
                if (isApiCalled) {
                    receivedJoinList.addAll(response.enrollInfoList)
                }
                val adapter = binding.rvReceivedJoinList.adapter as ReceivedJoinAdapter
                adapter.updateList(receivedJoinList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }
        receivedJoinViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Log.i("Received Join Err", msg)
            }
        }
        receivedJoinViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.receivedJoinFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_receivedJoinFragment_to_loginFragment, bundle, navOptions)
            }
        }
    }

    override fun onReceivedEnrollClick(boardId: Long) {
        val dialog = ReceivedEnrollScrollDialogFragment.newInstance(boardId)
        dialog.show(parentFragmentManager, "ReceivedEnrollDialog")
    }
}
