package com.catchmate.presentation.view.mypage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.enumclass.AlarmType
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentNotificationSettingBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.activity.MainActivity
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.NotificationSettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingFragment : BaseFragment<FragmentNotificationSettingBinding>(FragmentNotificationSettingBinding::inflate) {
    private val notificationSettingViewModel: NotificationSettingViewModel by viewModels()
    private var isAllChecked = false
    private var isChatChecked = false
    private var isEnrollChecked = false
    private var isEventChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isAllChecked = it.getString("allAlarm", "") == "Y"
            isChatChecked = it.getString("chatAlarm", "") == "Y"
            isEnrollChecked = it.getString("enrollAlarm", "") == "Y"
            isEventChecked = it.getString("eventAlarm", "") == "Y"
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val mainActivity = requireActivity() as MainActivity
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (mainActivity.checkSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("알림 권한 상태", "허용됨")
                initViewModel()
                initView()
            } else {
                Log.d("알림 권한 상태", "거부됨")
                mainActivity.showPermissionRationaleDialog(
                    onCancelled = {
                        findNavController().popBackStack()
                    },
                )
            }
        }
    }

    private fun initView() {
        binding.apply {
            layoutHeaderNotificationSetting.apply {
                tvHeaderTextTitle.setText(R.string.mypage_setting_notification_setting)
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            // 초기 스위치 상태 설정
            switchNotificationSettingAll.isChecked = isAllChecked
            switchNotificationSettingChatting.isChecked = isChatChecked
            switchNotificationSettingEnroll.isChecked = isEnrollChecked
            switchNotificationSettingEvent.isChecked = isEventChecked
            // 스위치 클릭 이벤트 설정
            layoutNotificationSettingAllAlarm.setOnClickListener {
                if (isAllChecked) { // t -> f
                    isAllChecked = false
                    isChatChecked = false
                    isEnrollChecked = false
                    isEventChecked = false
                    switchNotificationSettingAll.isChecked = false
                    switchNotificationSettingChatting.isChecked = false
                    switchNotificationSettingEnroll.isChecked = false
                    switchNotificationSettingEvent.isChecked = false
                    notificationSettingViewModel.patchUserAlarm(AlarmType.ALL.name, "N")
                } else { // f -> t
                    isAllChecked = true
                    switchNotificationSettingAll.isChecked = true
                    notificationSettingViewModel.patchUserAlarm(AlarmType.ALL.name, "Y")
                    // false 였던 스위치만 api 호출
                    if (!isChatChecked) {
                        isChatChecked = true
                        switchNotificationSettingChatting.isChecked = true
                    }
                    if (!isEnrollChecked) {
                        isEnrollChecked = true
                        switchNotificationSettingEnroll.isChecked = true
                    }
                    if (!isEventChecked) {
                        isEventChecked = true
                        switchNotificationSettingEvent.isChecked = true
                    }
                }
            }
            layoutNotificationSettingChatAlarm.setOnClickListener {
                switchNotificationSettingChatting.isChecked = !switchNotificationSettingChatting.isChecked
                isChatChecked = switchNotificationSettingChatting.isChecked
                val isEnabled = if (isChatChecked) "Y" else "N"
                notificationSettingViewModel.patchUserAlarm(AlarmType.CHAT.name, isEnabled)
                updateSwitchState()
            }
            layoutNotificationSettingEnrollAlarm.setOnClickListener {
                switchNotificationSettingEnroll.isChecked = !switchNotificationSettingEnroll.isChecked
                isEnrollChecked = switchNotificationSettingEnroll.isChecked
                val isEnabled = if (isEnrollChecked) "Y" else "N"
                notificationSettingViewModel.patchUserAlarm(AlarmType.ENROLL.name, isEnabled)
                updateSwitchState()
            }
            layoutNotificationSettingEventAlarm.setOnClickListener {
                switchNotificationSettingEvent.isChecked = !switchNotificationSettingEvent.isChecked
                isEventChecked = switchNotificationSettingEvent.isChecked
                val isEnabled = if (isEventChecked) "Y" else "N"
                notificationSettingViewModel.patchUserAlarm(AlarmType.EVENT.name, isEnabled)
                updateSwitchState()
            }
        }
    }

    private fun updateSwitchState() {
        if (isChatChecked && isEnrollChecked && isEventChecked && !isAllChecked) {
            isAllChecked = true
            binding.switchNotificationSettingAll.isChecked = isAllChecked
        } else {
            if (isAllChecked) {
                isAllChecked = false
                binding.switchNotificationSettingAll.isChecked = isAllChecked
            }
        }
    }

    private fun initViewModel() {
        notificationSettingViewModel.patchUserAlarmResponse.observe(viewLifecycleOwner) { reponse ->
            reponse?.let {
                Log.i("설정완료", "${reponse.userId} / ${reponse.alarmType} / ${reponse.isEnabled}")
            }
        }
        notificationSettingViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.notificationSettingFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_notificationSettingFragment_to_loginFragment, bundle, navOptions)
            }
        }
        notificationSettingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }
}
