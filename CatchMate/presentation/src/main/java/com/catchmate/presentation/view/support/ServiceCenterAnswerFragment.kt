package com.catchmate.presentation.view.support

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentServiceCenterAnswerBinding
import com.catchmate.presentation.util.DateUtils.formatInquiryAnsweredDate
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.ServiceCenterAnswerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceCenterAnswerFragment : BaseFragment<FragmentServiceCenterAnswerBinding>(FragmentServiceCenterAnswerBinding::inflate) {
    private val serviceCenterAnswerViewModel: ServiceCenterAnswerViewModel by viewModels()
    private val inquiryId by lazy { arguments?.getLong("inquiryId") ?: -1L }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        serviceCenterAnswerViewModel.getInquiry(inquiryId)
        initView()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderServiceCenterAnswer.tvHeaderTextTitle.text = getString(R.string.notification_title)
            layoutHeaderServiceCenterAnswer.imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViewModel() {
        serviceCenterAnswerViewModel.getInquiryResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                binding.apply {
                    val title = getString(R.string.service_center_answer_title)
                    tvTitleServiceCenterAnswer.text = title.format(response.nickName)
                    tvDateServiceCenterAnswer.text = formatInquiryAnsweredDate(response.createdAt)
                    tvInquiryContentServiceCenterAnswer.text = response.content
                    tvAnswerContentServiceCenterAnswer.text = response.answer
                }
            }
        }
        serviceCenterAnswerViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.serviceCenterAnswerFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_serviceCenterAnswerFragment_to_loginFragment, bundle, navOptions)
            }
        }
        serviceCenterAnswerViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }
}
