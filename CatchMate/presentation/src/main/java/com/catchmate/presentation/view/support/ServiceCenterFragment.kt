package com.catchmate.presentation.view.support

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.enumclass.InquiryType
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentServiceCenterBinding
import com.catchmate.presentation.view.base.BaseFragment

class ServiceCenterFragment : BaseFragment<FragmentServiceCenterBinding>(FragmentServiceCenterBinding::inflate) {
    private val nickname by lazy { arguments?.getString("nickname") ?: "" }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            val title = getString(R.string.service_center_title)
            tvTitleServiceCenter.text = title.format(nickname)

            layoutHeaderServiceCenter.apply {
                tvHeaderTextTitle.text = getString(R.string.mypage_service_center)
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }

            tvAccountLoginServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("inquiryType", InquiryType.ACCOUNT.name)
                findNavController().navigate(R.id.action_serviceCenterFragment_to_serviceCenterInquiryFragment, bundle)
            }

            tvBoardServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("inquiryType", InquiryType.POST.name)
                findNavController().navigate(R.id.action_serviceCenterFragment_to_serviceCenterInquiryFragment, bundle)
            }

            tvChattingServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("inquiryType", InquiryType.CHAT.name)
                findNavController().navigate(R.id.action_serviceCenterFragment_to_serviceCenterInquiryFragment, bundle)
            }

            tvUserServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("inquiryType", InquiryType.USER.name)
                findNavController().navigate(R.id.action_serviceCenterFragment_to_serviceCenterInquiryFragment, bundle)
            }

            tvEtcServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("inquiryType", InquiryType.OTHER.name)
                findNavController().navigate(R.id.action_serviceCenterFragment_to_serviceCenterInquiryFragment, bundle)
            }
        }
    }
}
