package com.catchmate.presentation.view.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentEditProfileWatchStyleBottomSheetBinding
import com.catchmate.presentation.interaction.OnEditProfileWatchStyleSelectedListener
import com.catchmate.presentation.view.onboarding.CheerStyleButtonView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileWatchStyleBottomSheetFragment(
    private val defaultStyle: String,
    private val listener: OnEditProfileWatchStyleSelectedListener,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentEditProfileWatchStyleBottomSheetBinding? = null
    val binding get() = _binding!!

    private var selectedButton: CheerStyleButtonView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val behavior = dialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditProfileWatchStyleBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initCheerStyleButtons()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFooter() {
        binding.layoutFooterEditProfileWatchStyle.btnFooterOne.apply {
            text = getString(R.string.complete)
            setOnClickListener {
                listener.onWatchStyleSelected(
                    selectedButton
                        ?.binding
                        ?.tvCheerStyleName
                        ?.text
                        .toString()
                        .replace(" 스타일", ""),
                )
                dismiss()
            }
        }
    }

    private fun initCheerStyleButtons() {
        val cheerStyleButtons: List<CheerStyleButtonView> =
            listOf(
                binding.csbvEditProfileDirector,
                binding.csbvEditProfileMotherBird,
                binding.csbvEditProfileCheerLeader,
                binding.csbvEditProfileGlutton,
                binding.csbvEditProfileStone,
                binding.csbvEditProfileBodhisattva,
            )

        cheerStyleButtons.forEach { btn ->
            btn.binding.toggleCheerStyle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedButton?.binding?.toggleCheerStyle?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterEditProfileWatchStyle.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterEditProfileWatchStyle.btnFooterOne.isEnabled = false
                }
            }
            if (defaultStyle.isNotEmpty() && btn.binding.tvCheerStyleName.text == "$defaultStyle 스타일") {
                btn.binding.toggleCheerStyle.isChecked = true
            }
        }
    }
}
