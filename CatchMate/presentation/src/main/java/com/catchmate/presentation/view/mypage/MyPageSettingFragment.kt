package com.catchmate.presentation.view.mypage

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentMyPageSettingBinding
import com.catchmate.presentation.view.base.BaseFragment

class MyPageSettingFragment : BaseFragment<FragmentMyPageSettingBinding>(FragmentMyPageSettingBinding::inflate) {
    private val email by lazy { arguments?.getString("email") ?: "" }
    private val nickname by lazy { arguments?.getString("nickname") ?: "" }
    private val allAlarm by lazy { arguments?.getString("allAlarm") ?: "" }
    private val chatAlarm by lazy { arguments?.getString("chatAlarm") ?: "" }
    private val enrollAlarm by lazy { arguments?.getString("enrollAlarm") ?: "" }
    private val eventAlarm by lazy { arguments?.getString("eventAlarm") ?: "" }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        initMenu()
    }

    private fun initHeader() {
        binding.layoutHeaderMyPageSetting.apply {
            tvHeaderTextTitle.setText(R.string.mypage_setting)
            imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initMenu() {
        binding.apply {
            tvMyPageSettingUserInfo.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("email", email)
                findNavController().navigate(R.id.action_myPageSettingFragment_to_accountInfoFragment, bundle)
            }
            tvMyPageSettingInformation.setOnClickListener {
                findNavController().navigate(R.id.action_myPageSettingFragment_to_informationFragment)
            }
            tvMyPageSettingServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("nickname", nickname)
                findNavController().navigate(R.id.action_myPageSettingFragment_to_serviceCenterFragment)
            }
            tvMyPageSettingBlockedSetting.setOnClickListener {
                findNavController().navigate(R.id.action_myPageSettingFragment_to_blockedSettingFragment)
            }
            tvMyPageSettingTemrsAndConditions.setOnClickListener {
                findNavController().navigate(R.id.action_myPageSettingFragment_to_termsAndPoliciesFragment)
            }
            tvMyPageSettingNotificationSetting.setOnClickListener {
                val bundle =
                    Bundle().apply {
                        putString("allAlarm", allAlarm)
                        putString("chatAlarm", chatAlarm)
                        putString("enrollAlarm", enrollAlarm)
                        putString("eventAlarm", eventAlarm)
                    }
                findNavController().navigate(R.id.action_myPageSettingFragment_to_notificationSettingFragment, bundle)
            }
            tvMyPageSettingAnnouncement.setOnClickListener {
                findNavController().navigate(R.id.action_myPageSettingFragment_to_announcementFragment)
            }
        }
    }
}
