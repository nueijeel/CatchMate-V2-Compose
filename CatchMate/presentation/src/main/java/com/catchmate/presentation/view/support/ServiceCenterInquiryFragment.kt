package com.catchmate.presentation.view.support

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentServiceCenterInquiryBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.ServiceCenterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceCenterInquiryFragment : BaseFragment<FragmentServiceCenterInquiryBinding>(FragmentServiceCenterInquiryBinding::inflate) {
    private val inquiryType by lazy { arguments?.getString("inquiryType") ?: "" }
    private val serviceCenterViewModel: ServiceCenterViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
    }

    private fun initView() {
        binding.apply {
            imgbtnBackServiceCenterInquiry.setOnClickListener {
                findNavController().popBackStack()
            }

            layoutFooterServiceCenterInquiry.btnFooterOne.apply {
                text = getString(R.string.service_center_inquiry_apply_btn)
                setOnClickListener {
                    val request =
                        PostInquiryRequest(
                            inquiryType,
                            edtContentServiceCenterInquiry.text.toString(),
                        )
                    serviceCenterViewModel.postInquiry(request)
                }
            }

            edtContentServiceCenterInquiry.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                        val currentLength = s?.length ?: 0
                        tvContentLetterCountServiceCenterInquiry.text = currentLength.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val inputText = s?.toString()?.trim()
                        layoutFooterServiceCenterInquiry.btnFooterOne.isEnabled = !inputText.isNullOrEmpty()
                    }
                },
            )
        }
    }

    private fun initViewModel() {
        serviceCenterViewModel.postInquiryResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                Snackbar.make(requireView(), R.string.service_center_inquiry_complete, Snackbar.LENGTH_SHORT).show()
                binding.edtContentServiceCenterInquiry.setText(R.string.all_empty_string)
            }
        }
        serviceCenterViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.serviceCenterInquiryFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_serviceCenterInquiryFragment_to_loginFragment, bundle, navOptions)
            }
        }
        serviceCenterViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }
}
