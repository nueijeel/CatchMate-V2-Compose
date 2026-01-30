package com.catchmate.presentation.view.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.auth.UserEntity
import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentLoginBinding
import com.catchmate.presentation.databinding.LayoutAlertDialogBinding
import com.catchmate.presentation.util.ReissueUtil.NAVIGATE_CODE_REISSUE
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.LocalDataViewModel
import com.catchmate.presentation.viewmodel.LoginViewModel
import com.catchmate.presentation.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val navigateCode by lazy { arguments?.getInt("navigateCode") }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        enableDoubleBackPressedExit = true
        initViewModel()
        initView()
        if (navigateCode == NAVIGATE_CODE_REISSUE) showAlertDialog()
    }

    private fun initViewModel() {
        loginViewModel.resultTriple.observe(viewLifecycleOwner) { resultTriple ->
            if (resultTriple.first.isNotEmpty() && resultTriple.second.isNotEmpty()) {
                if (resultTriple.third) { // 신규 가입일 경우
                    val user = UserEntity(
                        email = resultTriple.second,
                        profileImage = null,
                        nickname = "",
                        favoriteClubId = 0,
                        gender = "",
                        watchStyle = null,
                        fcmToken = "",
                        birthDate = "",
                    )
                    loginViewModel.saveUserData(resultTriple.first, user)
                } else { // 기존에 가입한 적 있을 경우
                    // 로컬 데이터 닉네임 필드 값 isEmpty
                    // T: AddInfoFragment 이동
                    // F: HomeFragment 이동

                }
            }
        }
        // saveUserData Result success일 경우 datastore에 데이터 저장 후 addinfo page로 이동
    }

    private fun initView() {
        binding.apply {
            tvLoginGuest.setOnClickListener {
                mainViewModel.setGuestLogin(true)
                localDataViewModel.saveAccessToken("")
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
            cvLogin.setOnClickListener {
                loginViewModel.signWithGoogle(requireActivity())
            }
        }
    }

    private fun showAlertDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutAlertDialogBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.apply {
            tvAlertDialogTitle.setText(R.string.login_information_expired)
            tvAlertDialogPositive.apply {
                setText(R.string.complete)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}
