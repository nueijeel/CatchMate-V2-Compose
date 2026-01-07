package com.catchmate.presentation.view.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentTermsAndPoliciesBinding
import com.catchmate.presentation.view.base.BaseFragment

class TermsAndPoliciesFragment : BaseFragment<FragmentTermsAndPoliciesBinding>(FragmentTermsAndPoliciesBinding::inflate) {
    private lateinit var url: String

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderTemrsAndPolicies.apply {
                tvHeaderTextTitle.setText(R.string.terms_and_policies_title)
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            // 개인 정보 처리 방침
            tvPersonalInfoTermsAndPolicies.setOnClickListener {
                url = "https://catchmate.notion.site/19690504ec15804ba163fcf8fa0ab937"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            // 서비스 이용 약관
            tvServicePolicyTermsAndPolicies.setOnClickListener {
                url = "https://catchmate.notion.site/19690504ec15803588a7ca69b306bf3e"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            // 커뮤니티 이용 가이드
            tvCommunityGuideTermsAndPolicies.setOnClickListener {
                url = "https://catchmate.notion.site/19690504ec1580d6b2d0e38eb46c5536"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }
}
