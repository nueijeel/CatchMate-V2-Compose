package com.catchmate.presentation.view.support

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentAnnouncementDetailBinding
import com.catchmate.presentation.util.DateUtils.formatInquiryAnsweredDate
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.AnnouncementDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnnouncementDetailFragment : BaseFragment<FragmentAnnouncementDetailBinding>(FragmentAnnouncementDetailBinding::inflate) {
    private val noticeId by lazy { arguments?.getLong("noticeId") ?: -1L }
    private val announcementDetailViewModel: AnnouncementDetailViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        announcementDetailViewModel.getNoticeDetail(noticeId)
        initView()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderAnnouncementDetail.apply {
                tvHeaderTextTitle.visibility = View.GONE
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initViewModel() {
        announcementDetailViewModel.getNoticeDetailResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                binding.apply {
                    tvTitleAnnouncementDetail.text = response.title
                    tvContentAnnouncementDetail.text = response.content
                    tvDateAnnouncementDetail.text = formatInquiryAnsweredDate(response.updatedAt)
                }
            }
        }
        announcementDetailViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.announcementDetailFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_announcementDetailFragment_to_loginFragment, bundle, navOptions)
            }
        }
        announcementDetailViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }
}
