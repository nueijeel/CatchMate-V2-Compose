package com.catchmate.presentation.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentMyPageBinding
import com.catchmate.presentation.util.AgeUtils
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.GenderUtils
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::inflate) {
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        enableDoubleBackPressedExit = true
        initViewModel()
        localDataViewModel.getAccessToken()
        initHeader()
        initViews()
    }

    private fun initHeader() {
        binding.layoutHeaderMyPage.apply {
            tvSettingHeaderTitle.setText(R.string.mypage_title)
            imgbtnSettingHeaderSetting.setOnClickListener {
                val bundle =
                    Bundle().apply {
                        putString("email", myPageViewModel.userProfile.value?.email)
                        putString("nickname", myPageViewModel.userProfile.value?.nickName)
                        putString("allAlarm", myPageViewModel.userProfile.value?.allAlarm)
                        putString("chatAlarm", myPageViewModel.userProfile.value?.chatAlarm)
                        putString("enrollAlarm", myPageViewModel.userProfile.value?.enrollAlarm)
                        putString("eventAlarm", myPageViewModel.userProfile.value?.eventAlarm)
                    }
                findNavController().navigate(R.id.action_myPageFragment_to_myPageSettingFragment, bundle)
            }
        }
    }

    private fun initProfile(userInfo: GetUserProfileResponse) {
        binding.viewMyPageProfile.binding.apply {
            Glide
                .with(this@MyPageFragment)
                .load(userInfo.profileImageUrl)
                .into(ivMyPageUserProfile)
            tvMyPageUserProfileNickname.text = userInfo.nickName
            tvMyPageUserProfileTeamBadge.text = ClubUtils.convertClubIdToName(userInfo.favoriteClub.id)

            DrawableCompat
                .setTint(
                    tvMyPageUserProfileTeamBadge.background,
                    convertTeamColor(
                        requireContext(),
                        userInfo.favoriteClub.id,
                        true,
                        "mypage",
                    ),
                )

            if (userInfo.watchStyle.isNullOrEmpty()) {
                tvMyPageUserProfileCheerStyleBadge.visibility = View.GONE
            } else {
                tvMyPageUserProfileCheerStyleBadge.text = userInfo.watchStyle
            }
            tvMyPageUserProfileGenderBadge.text = GenderUtils.convertBoardGender(requireContext(), userInfo.gender)
            tvMyPageUserProfileAgeBadge.text = AgeUtils.convertBirthDateToAge(userInfo.birthDate)
        }
    }

    private fun initViewModel() {
        myPageViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            initProfile(response)
        }
        myPageViewModel.newCount.observe(viewLifecycleOwner) { response ->
            if (response.newEnrollCount == 0) {
                binding.tvMyPageReceivedJoinUnreadCount.visibility = View.INVISIBLE
            } else {
                binding.tvMyPageReceivedJoinUnreadCount.apply {
                    visibility = View.VISIBLE
                    text = response.newEnrollCount.toString()
                }
            }
        }
        localDataViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token.isNullOrEmpty()) {
                binding.apply {
                    layoutHeaderMyPage.imgbtnSettingHeaderSetting.visibility = View.GONE
                    svMyPage.visibility = View.GONE
                    viewMyPageProfile.visibility = View.GONE
                    layoutGuestMyPage.visibility = View.VISIBLE
                }
            } else {
                myPageViewModel.getUserProfile()
                myPageViewModel.getEnrollNewCount()
            }
        }
        myPageViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.myPageFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_myPageFragment_to_loginFragment, bundle, navOptions)
            }
        }
        myPageViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
    }

    private fun initViews() {
        binding.apply {
            viewMyPageProfile.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("userInfo", myPageViewModel.userProfile.value)
                findNavController().navigate(R.id.action_myPageFragment_to_editProfileFragment, bundle)
            }
            tvMyPageMyPost.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("userInfo", myPageViewModel.userProfile.value)
                findNavController().navigate(R.id.action_myPageFragment_to_myPostFragment, bundle)
            }
            tvMyPageSentJoin.setOnClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_sentJoinFragment)
            }
            layoutMyPageReceivedJoin.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("newCount", myPageViewModel.newCount.value?.newEnrollCount ?: 0)
                findNavController().navigate(R.id.action_myPageFragment_to_receivedJoinFragment, bundle)
            }
            tvMyPageInformation.setOnClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_informationFragment)
            }
            tvMyPageServiceCenter.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("nickname", myPageViewModel.userProfile.value?.nickName)
                findNavController().navigate(R.id.action_myPageFragment_to_serviceCenterFragment, bundle)
            }
            tvMyPageTermsAndConditions.setOnClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_termsAndPoliciesFragment)
            }
            tvMyPageAnnouncement.setOnClickListener {
                findNavController().navigate(R.id.action_myPageFragment_to_announcementFragment)
            }
            layoutGuestMyPage.setOnClickListener {
                Log.d("눌림", "눌렸어")
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.myPageFragment, true)
                        .build()
                findNavController().navigate(R.id.action_myPageFragment_to_loginFragment, null, navOptions)
            }
        }
    }
}
