package com.catchmate.presentation.view.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentSignupCompleteBinding
import com.catchmate.presentation.view.base.BaseFragment

class SignupCompleteFragment : BaseFragment<FragmentSignupCompleteBinding>(FragmentSignupCompleteBinding::inflate) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        enableDoubleBackPressedExit = true
        initFooterButton()
    }

    private fun initFooterButton() {
        binding.layoutSignupCompleteButton.btnFooterOne.run {
            setText(R.string.finish)
            isEnabled = true
            setOnClickListener {
                findNavController().navigate(R.id.action_signupCompleteFragment_to_homeFragment)
            }
        }
    }
}
