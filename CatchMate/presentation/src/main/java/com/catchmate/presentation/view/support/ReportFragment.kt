package com.catchmate.presentation.view.support

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.enumclass.ReportType
import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentReportBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.view.post.TeamToggleCheckButtonView
import com.catchmate.presentation.viewmodel.ReportViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(FragmentReportBinding::inflate) {
    private val nickname by lazy { arguments?.getString("nickname") ?: "" }
    private val userId by lazy { arguments?.getLong("userId") ?: -1L }
    private val reportViewModel: ReportViewModel by viewModels()
    private var selectedButton: TeamToggleCheckButtonView? = null

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
            ivBackReport.setOnClickListener {
                findNavController().popBackStack()
            }
            val title = getString(R.string.report_title)
            tvTitleReport.text = title.format(nickname)
            layoutFooterReport.btnFooterOne.text = getString(R.string.report_btn)
            layoutFooterReport.btnFooterOne.setOnClickListener {
                // 신고 버튼 클릭 시 선택된 신고 타입과 edt 값(있으면) 넘기기
                val request =
                    PostUserReportRequest(
                        getSelectedReportType(),
                        edtContentReport.text.toString(),
                    )
                reportViewModel.postUserReport(userId, request)
            }
        }

        val teamToggleCheckButtons: List<TeamToggleCheckButtonView> =
            listOf(
                binding.toggleProfanityReport,
                binding.toggleDefamationReport,
                binding.togglePrivacyInvasionReport,
                binding.toggleSpamReport,
                binding.toggleAdvertisementReport,
                binding.toggleFalseInformationReport,
                binding.toggleOtherReport,
            )

        teamToggleCheckButtons.forEach { btn ->
            btn.binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedButton?.binding?.cbTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterReport.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterReport.btnFooterOne.isEnabled = false
                }
            }
            btn.setOnClickListener {
                btn.binding.cbTeamToggleCheckButton.isChecked = !btn.binding.cbTeamToggleCheckButton.isChecked
            }
        }
    }

    private fun initViewModel() {
        reportViewModel.postUserReportResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                Snackbar.make(requireView(), R.string.report_toast, Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        reportViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.reportFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_reportFragment_to_loginFragment, bundle, navOptions)
            }
        }
        reportViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun getSelectedReportType(): String {
        val text =
            selectedButton
                ?.binding
                ?.tvTeamToggleCheckButton
                ?.text
                .toString()
        return when (text) {
            ReportType.PROFANITY.text -> ReportType.PROFANITY.name
            ReportType.DEFAMATION.text -> ReportType.DEFAMATION.name
            ReportType.PRIVACY_INVASION.text -> ReportType.PRIVACY_INVASION.name
            ReportType.SPAM.text -> ReportType.SPAM.name
            ReportType.ADVERTISEMENT.text -> ReportType.ADVERTISEMENT.name
            ReportType.FALSE_INFORMATION.text -> ReportType.FALSE_INFORMATION.name
            else -> ReportType.OTHER.name
        }
    }
}
