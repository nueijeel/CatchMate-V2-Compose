package com.catchmate.presentation.view.onboarding

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.catchmate.domain.model.user.PostUserAdditionalInfoRequest
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentSignupBinding
import com.catchmate.presentation.util.DateUtils
import com.catchmate.presentation.view.base.BaseFragment
import com.catchmate.presentation.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {
    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var userInfo: PostUserAdditionalInfoRequest
    private val pushNotificationAgree by lazy { arguments?.getBoolean("PushNotificationAgree") ?: false }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private var isNicknameValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getUserInfo()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        initFooterBtn()
        initNickNameViews()
        initBirthDateEditText()
        initGenderChip()
    }

    private fun getUserInfo(): PostUserAdditionalInfoRequest =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("userInfo", PostUserAdditionalInfoRequest::class.java)!!
        } else {
            arguments?.getSerializable("userInfo") as PostUserAdditionalInfoRequest
        }

    private fun initFooterBtn() {
        binding.layoutSignupFooter.btnFooterOne.apply {
            setText(R.string.next)
            setOnClickListener {
                val nickName = binding.edtSignupNickname.text.toString()
                val birthDate = binding.edtSignupBirth.text.toString()
                val gender = binding.chipgroupSignupGender.checkedChipId

                if (nickName.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                if (birthDate.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                if (gender == null) {
                    return@setOnClickListener
                }

                val newUserInfo =
                    PostUserAdditionalInfoRequest(
                        userInfo.email,
                        userInfo.providerId,
                        userInfo.provider,
                        userInfo.profileImageUrl,
                        userInfo.fcmToken,
                        if (gender == R.id.chip_signup_male) "M" else "F",
                        nickName,
                        DateUtils.formatBirthDate(birthDate),
                        -1,
                        "",
                    )
                val bundle = Bundle()
                bundle.putSerializable("userInfo", newUserInfo)
                bundle.putBoolean("PushNotificationAgree", pushNotificationAgree)

                findNavController().navigate(R.id.action_signupFragment_to_teamOnboardingFragment, bundle)
            }
        }
    }

    private fun initHeader() {
        binding.layoutSignupHeader.apply {
            imgbtnOnboardingIndicator2.apply {
                setImageResource(R.drawable.vec_onboarding_indicator_activated_6dp)
            }
            imgbtnOnboardingBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initNickNameViews() {
        binding.apply {
            // 닉네임 길이 기본값 설정
            if (tvSignupNicknameCount.text.isNullOrEmpty()) {
                tvSignupNicknameCount.text = "0"
            }

            // 닉네임 edt 포커스 여부에 따른 delete icon 표시 여부 설정
            edtSignupNickname.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    imgbtnSignupDeleteInput.visibility = View.VISIBLE
                } else {
                    imgbtnSignupDeleteInput.visibility = View.INVISIBLE
                }
            }

            // delete icon 클릭 이벤트
            imgbtnSignupDeleteInput.setOnClickListener {
                edtSignupNickname.text.clear()
            }

            // 닉네임 edt 텍스트 변경 리스너
            edtSignupNickname.addTextChangedListener(
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
                        tvSignupNicknameCount.text = currentLength.toString()
                        runnable?.let { handler.removeCallbacks(it) }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val inputText = s?.toString()?.trim()

                        if (!inputText.isNullOrEmpty()) {
                            runnable =
                                kotlinx.coroutines.Runnable {
                                    s?.toString()?.let {
                                        checkNicknameAvailability(inputText)
                                    }
                                }
                            handler.postDelayed(runnable!!, 500)
                        } else {
                            tvSignupNicknameAlert.visibility = View.INVISIBLE
                        }
                    }
                },
            )
        }
    }

    private fun initBirthDateEditText() {
        binding.edtSignupBirth.doAfterTextChanged {
            checkFieldsAreEmpty()
        }
    }

    private fun initGenderChip() {
        binding.chipSignupMale.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                binding.chipSignupFemale.isChecked = true
            }
        }
        binding.chipSignupFemale.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                binding.chipSignupMale.isChecked = true
            }
        }
    }

    private fun checkNicknameAvailability(nickName: String) {
        signUpViewModel.getAuthCheckNickname(nickName)
        signUpViewModel.getCheckNicknameResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.tvSignupNicknameAlert.apply {
                    visibility = View.VISIBLE
                    if (response.available) {
                        setText(R.string.signup_nickname_usable)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.system_blue))
                        isNicknameValid = true
                        checkFieldsAreEmpty()
                    } else {
                        setText(R.string.signup_nickname_unusable)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.system_red))
                        isNicknameValid = false
                        checkFieldsAreEmpty()
                    }
                }
            }
        }
    }

    private fun checkFieldsAreEmpty() {
        val nickName = binding.edtSignupNickname.text.toString()
        val birthDate = binding.edtSignupBirth.text.toString()

        binding.layoutSignupFooter.btnFooterOne.isEnabled =
            nickName.isNotEmpty() &&
            birthDate.isNotEmpty() &&
            isNicknameValid
    }
}
