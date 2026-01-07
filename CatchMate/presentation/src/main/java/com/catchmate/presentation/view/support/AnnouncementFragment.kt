package com.catchmate.presentation.view.support

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentAnnouncementBinding
import com.catchmate.presentation.interaction.OnAnnouncementItemClickListener
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.AnnouncementViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnnouncementFragment :
    BaseFragment<FragmentAnnouncementBinding>(FragmentAnnouncementBinding::inflate),
    OnAnnouncementItemClickListener {
    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private var announcementListAdapter: AnnouncementListAdapter? = null

    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var announcementList: MutableList<NoticeInfo> = mutableListOf()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
        getNoticeList()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderAnnouncement.apply {
                tvHeaderTextTitle.setText(R.string.mypage_announcement)
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            announcementListAdapter = AnnouncementListAdapter(this@AnnouncementFragment)
            rvAnnouncement.apply {
                adapter = announcementListAdapter
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
                                getNoticeList()
                            }
                        }
                    },
                )
            }
        }
    }

    private fun initViewModel() {
        announcementViewModel.getNoticeListResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.rvAnnouncement.visibility = View.GONE
                binding.layoutAnnouncementNoList.visibility = View.VISIBLE
            } else {
                binding.rvAnnouncement.visibility = View.VISIBLE
                binding.layoutAnnouncementNoList.visibility = View.GONE
                if (isApiCalled) {
                    announcementList.addAll(response.noticeInfoList)
                }
                announcementListAdapter?.submitList(response.noticeInfoList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }
        announcementViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.announcementFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_announcementFragment_to_loginFragment, bundle, navOptions)
            }
        }
        announcementViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun getNoticeList() {
        if (isLoading || isLastPage) return
        isLoading = true
        announcementViewModel.getNoticeList(currentPage)
        isApiCalled = true
    }

    override fun onAnnouncementItemClick(noticeId: Long) {
        val bundle = Bundle()
        bundle.putLong("noticeId", noticeId)
        findNavController().navigate(R.id.action_announcementFragment_to_announcementDetailFragment, bundle)
    }
}
