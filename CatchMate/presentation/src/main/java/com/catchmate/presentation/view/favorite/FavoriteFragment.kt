package com.catchmate.presentation.view.favorite

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.board.Board
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentFavoriteBinding
import com.catchmate.presentation.interaction.OnListItemAllRemovedListener
import com.catchmate.presentation.interaction.OnPostItemClickListener
import com.catchmate.presentation.interaction.OnPostItemToggleClickListener
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment :
    BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate),
    OnPostItemClickListener,
    OnPostItemToggleClickListener,
    OnListItemAllRemovedListener {
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    private var currentPage: Int = 0
    private var isLastPage = false
    private var isLoading = false
    private var isApiCalled = false
    private var isFirstLoad = true
    private var likedList: MutableList<Board> = mutableListOf()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        enableDoubleBackPressedExit = true
        initHeader()
        initViewModel()
        initRecyclerView()

        if (isFirstLoad) {
            getLikedBoard()
            isFirstLoad = false
        }
    }

    private fun initHeader() {
        binding.layoutHeaderFavorite.apply {
            tvHeaderTextTitle.setText(R.string.favorite_title)
            imgbtnHeaderTextBack.visibility = View.GONE
        }
    }

    private fun initViewModel() {
        favoriteViewModel.getLikedBoardResponse.observe(viewLifecycleOwner) { response ->
            if (response.isFirst && response.isLast && response.totalElements == 0) {
                binding.layoutFavoriteNoList.visibility = View.VISIBLE
                binding.rvFavoritePost.visibility = View.GONE
            } else {
                binding.layoutFavoriteNoList.visibility = View.GONE
                binding.rvFavoritePost.visibility = View.VISIBLE
                if (isApiCalled) {
                    likedList.addAll(response.boardInfoList)
                }
                val adapter = binding.rvFavoritePost.adapter as FavoritePostAdapter
                adapter.updateLikedList(likedList)
                isLastPage = response.isLast
                isLoading = false
            }
            isApiCalled = false
        }

        favoriteViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.favoriteFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_favoriteFragment_to_loginFragment, bundle, navOptions)
            }
        }

        favoriteViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (it == "ListLoadError") {
                    binding.rvFavoritePost.visibility = View.GONE
                    binding.layoutFavoriteNoList.visibility = View.VISIBLE
                    Glide
                        .with(requireContext())
                        .load(R.drawable.vec_all_list_error_icon)
                        .into(binding.ivFavoriteNoList)
                    binding.tvFavoriteNoListTitle.setText(R.string.all_error_page_title)
                    binding.tvFavoriteNoListSub.visibility = View.GONE
                } else {
                    Snackbar.make(requireView(), R.string.all_component_error_msg, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        favoriteViewModel.deleteBoardLikeResponse.observe(viewLifecycleOwner) { response ->
            if (!response.state) {
                Snackbar.make(requireView(), R.string.all_component_error_msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLikedBoard() {
        if (isLoading || isLastPage) return
        isLoading = true
        favoriteViewModel.getLikedBoard(currentPage)
        isApiCalled = true
    }

    private fun initRecyclerView() {
        binding.rvFavoritePost.apply {
            adapter =
                FavoritePostAdapter(
                    requireContext(),
                    layoutInflater,
                    this@FavoriteFragment,
                    this@FavoriteFragment,
                    this@FavoriteFragment,
                )
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
                            getLikedBoard()
                        }
                    }
                },
            )
        }
    }

    override fun onPostItemClicked(boardId: Long) {
        val bundle = Bundle()
        bundle.putLong("boardId", boardId)
        findNavController().navigate(R.id.action_favoriteFragment_to_readPostFragment, bundle)
    }

    override fun onPostItemToggleClicked(
        boardId: Long,
        position: Int,
    ) {
        favoriteViewModel.deleteBoardLike(boardId)
        likedList.removeAt(position)
        val adapter = binding.rvFavoritePost.adapter as FavoritePostAdapter
        adapter.removeUnlikedPost(position)
    }

    override fun onListItemAllRemoved() {
        binding.rvFavoritePost.visibility = View.GONE
        binding.layoutFavoriteNoList.visibility = View.VISIBLE
    }
}
