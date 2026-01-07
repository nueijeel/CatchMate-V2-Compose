package com.catchmate.presentation.view.onboarding

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.enumclass.AlarmType
import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentCheerStyleOnboardingBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.MainViewModel
import com.catchmate.presentation.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheerStyleOnboardingFragment : BaseFragment<FragmentCheerStyleOnboardingBinding>(FragmentCheerStyleOnboardingBinding::inflate) {
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var userInfo: PostUserAdditionalInfoRequest
    private val pushNotificationAgree by lazy { arguments?.getBoolean("PushNotificationAgree") ?: false }

    private var selectedButton: CheerStyleButtonView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getUserInfo()
        Log.i("userInfo", "${userInfo.nickName},${userInfo.gender},${userInfo.birthDate},${userInfo.favoriteClubId}")
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initTitle()
        initHeader()
        initFooterButton()
        initCheerStyleButtons()
    }

    private fun getUserInfo(): PostUserAdditionalInfoRequest =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("userInfo", PostUserAdditionalInfoRequest::class.java)!!
        } else {
            arguments?.getSerializable("userInfo") as PostUserAdditionalInfoRequest
        }

    private fun initTitle() {
        val title = getString(R.string.team_onboarding_title1)
        binding.tvCheerStyleOnboardingTitle1.text = title.format(userInfo.nickName)
    }

    private fun initFooterButton() {
        binding.layoutCheerStyleOnboardingNext.btnFooterOne.apply {
            isEnabled = true
            setText(R.string.next)
            setOnClickListener {
                val newUserInfo =
                    PostUserAdditionalInfoRequest(
                        userInfo.email,
                        userInfo.providerId,
                        userInfo.provider,
                        userInfo.profileImageUrl,
                        userInfo.fcmToken,
                        userInfo.gender,
                        userInfo.nickName,
                        userInfo.birthDate,
                        userInfo.favoriteClubId,
                        watchStyle =
                            if (selectedButton
                                    ?.binding
                                    ?.tvCheerStyleName
                                    ?.text
                                    ?.toString() == null
                            ) {
                                ""
                            } else {
                                selectedButton
                                    ?.binding
                                    ?.tvCheerStyleName
                                    ?.text
                                    ?.toString()!!
                                    .replace(" 스타일", "")
                            },
                    )
                signUpViewModel.postUserAdditionalInfo(newUserInfo)
            }
        }
    }

    private fun initHeader() {
        binding.layoutCheerStyleOnboardingHeader.apply {
            imgbtnOnboardingIndicator4.setImageResource(R.drawable.vec_onboarding_indicator_activated_6dp)
            imgbtnOnboardingBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initCheerStyleButtons() {
        val cheerStyleButtons: List<CheerStyleButtonView> =
            listOf(
                binding.csbvCheerStyleOnboardingDirector,
                binding.csbvCheerStyleOnboardingMotherBird,
                binding.csbvCheerStyleOnboardingCheerLeader,
                binding.csbvCheerStyleOnboardingGlutton,
                binding.csbvCheerStyleOnboardingStone,
                binding.csbvCheerStyleOnboardingBodhisattva,
            )

        cheerStyleButtons.forEach { btn ->
            btn.binding.toggleCheerStyle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedButton?.binding?.toggleCheerStyle?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                }
            }
        }
    }

    private fun initViewModel() {
        signUpViewModel.userAdditionalInfoResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("response", "${response.userId}\n${response.accessToken}\n${response.refreshToken}")
                localDataViewModel.saveAccessToken(response.accessToken)
                localDataViewModel.saveRefreshToken(response.refreshToken)
                localDataViewModel.saveUserId(response.userId)
                localDataViewModel.saveProvider(userInfo.provider)
                mainViewModel.setGuestLogin(false)
                val isEnabled = if (pushNotificationAgree) "Y" else "N"
                signUpViewModel.patchUserAlarm(AlarmType.ALL.name, isEnabled)
                findNavController().navigate(R.id.action_cheerStyleOnboardingFragment_to_signupCompleteFragment)
            }
        }
        signUpViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                if (isTrue) {
                    val navOptions =
                        NavOptions
                            .Builder()
                            .setPopUpTo(R.id.cheerStyleOnboardingFragment, true)
                            .build()
                    val bundle = Bundle()
                    bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                    findNavController().navigate(R.id.action_cheerStyleOnboardingFragment_to_loginFragment, bundle, navOptions)
                }
            }
        }
        signUpViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Log.i("SIGN UP ERR", message.toString())
        }
        signUpViewModel.patchUserAlarmResponse.observe(viewLifecycleOwner) { response ->
            Log.i("알림 설정 완료", "${response.alarmType} - ${response.isEnabled}")
        }
    }
}
