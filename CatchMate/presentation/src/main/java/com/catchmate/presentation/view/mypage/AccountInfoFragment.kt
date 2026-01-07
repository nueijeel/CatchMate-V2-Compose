package com.catchmate.presentation.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentAccountInfoBinding
import com.catchmate.presentation.databinding.LayoutSimpleDialogBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.AccountInfoViewModel
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountInfoFragment : BaseFragment<FragmentAccountInfoBinding>(FragmentAccountInfoBinding::inflate) {
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private val accountInfoViewModel: AccountInfoViewModel by viewModels()
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email") ?: ""
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        localDataViewModel.getProvider()
        localDataViewModel.getRefreshToken()
        initHeader()
    }

    private fun initHeader() {
        binding.layoutHeaderAccountInfo.apply {
            tvHeaderTextTitle.setText(R.string.mypage_setting_user_info)
            imgbtnHeaderTextBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initLoginInfo(provider: String) {
        binding.apply {
            tvAccountInfoEmail.text = email
            val targetResource =
                when (provider) {
                    "kakao" -> R.drawable.vec_login_kakao
                    "naver" -> R.drawable.vec_login_naver
                    else -> R.drawable.vec_login_google
                }
            Glide
                .with(this@AccountInfoFragment)
                .load(targetResource)
                .into(ivAccountInfoLoginPlatform)
        }
    }

    private fun initViewModel() {
        localDataViewModel.provider.observe(viewLifecycleOwner) { provider ->
            initLoginInfo(provider)
        }
        localDataViewModel.refreshToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                initLogoutBtn()
                initWithdrawBtn()
            }
        }
        accountInfoViewModel.logoutResponse.observe(viewLifecycleOwner) { response ->
            Log.i("LOGOUT", response.state.toString())
            // 로그아웃 시 로컬 데이터 삭제 및 화면 이동
            localDataViewModel.logoutAndWithdraw()
            val navOptions =
                NavOptions
                    .Builder()
                    .setPopUpTo(R.id.accountInfoFragment, true)
                    .build()
            findNavController().navigate(R.id.action_accountInfoFragment_to_loginFragment, null, navOptions)
        }
        accountInfoViewModel.navigateToLogin.observe(viewLifecycleOwner) { isTrue ->
            if (isTrue) {
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.accountInfoFragment, true)
                        .build()
                val bundle = Bundle()
                bundle.putInt("navigateCode", NAVIGATE_CODE_REISSUE)
                findNavController().navigate(R.id.action_accountInfoFragment_to_loginFragment, bundle, navOptions)
            }
        }
        accountInfoViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("Reissue Error", it)
            }
        }
        accountInfoViewModel.withdrawResponse.observe(viewLifecycleOwner) { response ->
            if (response.state) {
                localDataViewModel.logoutAndWithdraw()
                val navOptions =
                    NavOptions
                        .Builder()
                        .setPopUpTo(R.id.accountInfoFragment, true)
                        .build()
                findNavController().navigate(R.id.action_accountInfoFragment_to_loginFragment, null, navOptions)
            }
        }
    }

    private fun initLogoutBtn() {
        binding.layoutFooterAccountInfo.btnFooterOne.apply {
            isEnabled = true
            setText(R.string.mypage_setting_user_logout)
            setOnClickListener {
                accountInfoViewModel.logout(localDataViewModel.refreshToken.value!!)
            }
        }
    }

    private fun initWithdrawBtn() {
        binding.tvAccountInfoDeleteAccount.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            val dialogBinding = LayoutSimpleDialogBinding.inflate(layoutInflater)

            builder.setView(dialogBinding.root)

            val dialog = builder.create()

            dialogBinding.apply {
                tvSimpleDialogTitle.setText(R.string.mypage_setting_delete_account_dialog_title)
                tvSimpleDialogNegative.apply {
                    setText(R.string.dialog_button_cancel)
                    setOnClickListener {
                        dialog.dismiss()
                    }
                }
                tvSimpleDialogPositive.apply {
                    setText(R.string.mypage_setting_delete_account_dialog_pov_btn)
                    setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.brand500),
                    )
                    setOnClickListener {
                        accountInfoViewModel.withdraw()
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
        }
    }
}
