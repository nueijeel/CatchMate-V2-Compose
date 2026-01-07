package com.catchmate.presentation.view.onboarding

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.enumclass.Club
import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentTeamOnboardingBinding
import com.catchmate.presentation.view.base.BaseFragment

class TeamOnboardingFragment : BaseFragment<FragmentTeamOnboardingBinding>(FragmentTeamOnboardingBinding::inflate) {
    private lateinit var userInfo: PostUserAdditionalInfoRequest
    private val pushNotificationAgree by lazy { arguments?.getBoolean("PushNotificationAgree") ?: false }

    private var selectedButton: TeamButtonView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getUserInfo()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTitle()
        initHeader()
        initFooterBtn()
        initTeamButtons()
    }

    private fun initTitle() {
        val title = getString(R.string.team_onboarding_title1)
        binding.tvTeamOnboardingTitle1.text = title.format(userInfo.nickName)
    }

    private fun initTeamButtons() {
        val teamButtons: List<TeamButtonView> =
            listOf(
                binding.tbvNc,
                binding.tbvSamsung,
                binding.tbvSsg,
                binding.tbvDoosan,
                binding.tbvKt,
                binding.tbvHanwha,
                binding.tbvLotte,
                binding.tbvKia,
                binding.tbvLg,
                binding.tbvKiwoom,
                binding.tbvPacifist,
                binding.tbvBaseballBeginner,
            )

        teamButtons.forEach { btn ->
            btn.binding.toggleTeamButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedButton?.binding?.toggleTeamButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutTeamOnboardingFooter.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutTeamOnboardingFooter.btnFooterOne.isEnabled = false
                }
            }
        }
    }

    private fun initHeader() {
        binding.layoutTeamOnboardingHeader.apply {
            imgbtnOnboardingIndicator3.setImageResource(R.drawable.vec_onboarding_indicator_activated_6dp)
            imgbtnOnboardingIndicator2.setOnClickListener {
                findNavController().popBackStack()
            }
            imgbtnOnboardingBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initFooterBtn() {
        binding.layoutTeamOnboardingFooter.btnFooterOne.apply {
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
                        getSelectedTeamId(
                            selectedButton
                                ?.binding
                                ?.tvTeamButton
                                ?.text
                                .toString(),
                        ),
                        "",
                    )
                val bundle = Bundle()
                bundle.putSerializable("userInfo", newUserInfo)
                bundle.putBoolean("PushNotificationAgree", pushNotificationAgree)
                findNavController().navigate(R.id.action_teamOnboardingFragment_to_cheerStyleOnboardingFragment, bundle)
            }
        }
    }

    private fun getUserInfo(): PostUserAdditionalInfoRequest =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("userInfo", PostUserAdditionalInfoRequest::class.java)!!
        } else {
            arguments?.getSerializable("userInfo") as PostUserAdditionalInfoRequest
        }

    private fun getSelectedTeamId(teamName: String): Int =
        when (teamName) {
            getString(R.string.team_kia_tigers) -> Club.KIA.id
            getString(R.string.team_samsung_lions) -> Club.SAMSUNG.id
            getString(R.string.team_lg_twins) -> Club.LG.id
            getString(R.string.team_doosan_bears) -> Club.DOOSAN.id
            getString(R.string.team_kt_wiz) -> Club.KT.id
            getString(R.string.team_ssg_landers) -> Club.SSG.id
            getString(R.string.team_lotte_giants) -> Club.LOTTE.id
            getString(R.string.team_hanwha_eagles) -> Club.HANWHA.id
            getString(R.string.team_nc_dinos) -> Club.NC.id
            getString(R.string.team_kiwoom_heroes) -> Club.KIWOOM.id
            getString(R.string.pacifist) -> Club.PACIFIST.id
            else -> Club.BEGINNER.id
        }
}
