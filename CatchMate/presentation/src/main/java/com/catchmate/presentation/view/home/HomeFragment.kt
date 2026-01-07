package com.catchmate.presentation.view.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.board.Board
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentHomeBinding
import com.catchmate.presentation.interaction.OnClubFilterSelectedListener
import com.catchmate.presentation.interaction.OnDateFilterSelectedListener
import com.catchmate.presentation.interaction.OnPersonFilterSelectedListener
import com.catchmate.presentation.interaction.OnPostItemClickListener
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.activity.MainActivity
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.HomeViewModel
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnPostItemClickListener,
    OnDateFilterSelectedListener,
    OnClubFilterSelectedListener,
    OnPersonFilterSelectedListener {
    private val homeViewModel: HomeViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()

    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var isFirstLoad = true
    private var postList: MutableList<Board> = mutableListOf()

    private var gameStartDate: String? = null
    private var maxPerson: Int? = null
    private var preferredTeamIdList: Array<Int>? = null
    private var notificationBadgeDrawable: BadgeDrawable? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        enableDoubleBackPressedExit = true
        initViewModel()
        localDataViewModel.getAccessToken()
        initDateFilter()
        initTeamFilter()
        initHeadCountFilter()
        initRecyclerView()
        Log.i("POST SIZE", postList.size.toString())
        if (isFirstLoad) {
            getBoardList()
            isFirstLoad = false
        }
        (requireActivity() as MainActivity).refreshNotificationStatus()
    }

    @OptIn(ExperimentalBadgeUtils::class)
    fun updateNotificationBadge(hasUnreadNotification: Boolean) {
        if (hasUnreadNotification) {
            if (notificationBadgeDrawable == null) {
                notificationBadgeDrawable = BadgeDrawable.create(requireContext())
                notificationBadgeDrawable?.apply {
                    backgroundColor = getColor(requireContext(), R.color.system_red) // 알림 색상 지정
                    isVisible = true
                    clearNumber()
                    horizontalOffset = 40
                    verticalOffset = 30
                }
            } else {
                notificationBadgeDrawable?.isVisible = true
            }

            notificationBadgeDrawable?.let { badge ->
                BadgeUtils.attachBadgeDrawable(badge, binding.layoutHeaderHome.imgbtnHeaderHomeNotification)
            }
        } else {
            // 뱃지 숨기기
            notificationBadgeDrawable?.isVisible = false
        }
    }

    private fun getTokens() {
        localDataViewModel.getUserId()
        localDataViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId == -1L) {
                getUserProfile()
            }
        }
    }

    private fun initHeader() {
        binding.layoutHeaderHome.apply {
            imgbtnHeaderHomeNotification.setOnClickListener {
                if (!localDataViewModel.accessToken.value.isNullOrEmpty()) {
                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                } else {
                    Snackbar.make(requireView(), R.string.all_guest_snackbar, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initViewModel() {
        localDataViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrEmpty()) {
                getTokens()
            }
            initHeader()
        }
        homeViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.homeFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment, bundle, navOptions)
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (it == "ListLoadError") {
                    binding.rvHomePosts.visibility = View.GONE
                    binding.layoutHomeNoList.visibility = View.VISIBLE
                    Glide
                        .with(requireContext())
                        .load(R.drawable.vec_all_list_error_icon)
                        .into(binding.ivHomeNoList)
                    binding.tvHomeNoList.setText(R.string.all_error_page_title)
                } else {
                    Snackbar.make(requireView(), R.string.all_component_error_msg, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.getBoardListResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.rvHomePosts.visibility = View.GONE
                binding.layoutHomeNoList.visibility = View.VISIBLE
            } else {
                binding.rvHomePosts.visibility = View.VISIBLE
                binding.layoutHomeNoList.visibility = View.GONE
                if (isApiCalled) {
                    postList.addAll(response.boardInfoList)
                    postList.forEach {
                        Log.i("LIST", "${it.boardId}")
                    }
                }
                val adapter = binding.rvHomePosts.adapter as HomePostAdapter
                adapter.updatePostList(postList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }
    }

    private fun getBoardList() {
        Log.i("api 호출", "호출 $isLoading $isLastPage")
        if (isLoading || isLastPage) return
        isLoading = true
        homeViewModel.getBoardList(
            gameStartDate,
            maxPerson,
            preferredTeamIdList,
            currentPage,
        )
        isApiCalled = true
    }

    private fun initDateFilter() {
        binding.hfvHomeDateFilter.setOnClickListener {
            val dateFilterBottomSheet = HomeDateFilterBottomSheetFragment(gameStartDate)
            dateFilterBottomSheet.setOnDateFilterSelectedListener(this@HomeFragment)
            dateFilterBottomSheet.show(requireActivity().supportFragmentManager, dateFilterBottomSheet.tag)
        }
        if (gameStartDate != null) {
            binding.hfvHomeDateFilter.setDateFilterText(gameStartDate)
            binding.hfvHomeDateFilter.setFilterTextColor(true)
        }
    }

    private fun initTeamFilter() {
        binding.hfvHomeTeamFilter.setOnClickListener {
            val teamFilterBottomSheet = HomeTeamFilterBottomSheetFragment(preferredTeamIdList)
            teamFilterBottomSheet.setOnClubSelectedListener(this@HomeFragment)
            teamFilterBottomSheet.show(requireActivity().supportFragmentManager, teamFilterBottomSheet.tag)
        }
        if (preferredTeamIdList != null) {
            binding.hfvHomeTeamFilter.setClubFilterText(preferredTeamIdList)
            binding.hfvHomeTeamFilter.setFilterTextColor(true)
        }
    }

    private fun initHeadCountFilter() {
        binding.hfvHomeMemberCountFilter.setOnClickListener {
            val headCountFilterBottomSheet = HomeHeadCountFilterBottomSheetFragment(maxPerson)
            headCountFilterBottomSheet.setOnPersonFilterSelected(this@HomeFragment)
            headCountFilterBottomSheet.show(requireActivity().supportFragmentManager, headCountFilterBottomSheet.tag)
        }
        if (maxPerson != null) {
            binding.hfvHomeMemberCountFilter.setFilterTextColor(true)
            binding.hfvHomeMemberCountFilter.setPersonFilterText(maxPerson)
        }
    }

    private fun initRecyclerView() {
        binding.rvHomePosts.apply {
            adapter = HomePostAdapter(requireContext(), layoutInflater, this@HomeFragment)
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

                        if (lastVisibleItemPosition + 1 >= itemTotalCount && !isLastPage && !isLoading) { // 새로운 목록 불러와야함
                            currentPage += 1
                            getBoardList()
                        }
                    }
                },
            )
        }
    }

    private fun getUserProfile() {
        homeViewModel.getUserProfile()
        homeViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            response?.let {
                localDataViewModel.saveUserId(response.userId)
            }
        }
    }

    override fun onPostItemClicked(boardId: Long) {
        if (localDataViewModel.accessToken.value.isNullOrEmpty()) {
            Snackbar.make(requireView(), R.string.all_guest_snackbar, Snackbar.LENGTH_SHORT).show()
        } else {
            val bundle = Bundle()
            bundle.putLong("boardId", boardId)
            findNavController().navigate(R.id.action_homeFragment_to_readPostFragment, bundle)
        }
    }

    override fun onDateSelected(date: String?) {
        gameStartDate = date
        currentPage = 0
        isLastPage = false
        isLoading = false
        postList.clear()
        getBoardList()
        binding.hfvHomeDateFilter.setDateFilterText(gameStartDate)
        binding.hfvHomeDateFilter.setFilterTextColor(gameStartDate != null)
    }

    override fun onClubFilterSelected(clubIdList: Array<Int>?) {
        preferredTeamIdList = clubIdList
        currentPage = 0
        isLastPage = false
        isLoading = false
        postList.clear()
        getBoardList()
        binding.hfvHomeTeamFilter.setClubFilterText(preferredTeamIdList)
        binding.hfvHomeTeamFilter.setFilterTextColor(preferredTeamIdList != null)
    }

    override fun onPersonFilterSelected(count: Int?) {
        maxPerson = count
        currentPage = 0
        isLastPage = false
        isLoading = false
        postList.clear()
        getBoardList()
        binding.hfvHomeMemberCountFilter.setFilterTextColor(count != null)
        binding.hfvHomeMemberCountFilter.setPersonFilterText(count)
    }
}
