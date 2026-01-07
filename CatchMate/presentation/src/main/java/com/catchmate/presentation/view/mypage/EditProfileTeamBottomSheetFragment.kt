package com.catchmate.presentation.view.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentEditProfileTeamBottomSheetBinding
import com.catchmate.presentation.interaction.OnEditProfileTeamSelectedListener
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.ClubUtils.convertClubNameToId
import com.catchmate.presentation.view.post.TeamToggleCheckButtonView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileTeamBottomSheetFragment(
    private val defaultClub: Int,
    private val listener: OnEditProfileTeamSelectedListener,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentEditProfileTeamBottomSheetBinding? = null
    val binding get() = _binding!!

    private var selectedButton: TeamToggleCheckButtonView? = null

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
        _binding = FragmentEditProfileTeamBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTeamButtonViews()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTeamButtonViews() {
        val teamToggleCheckButtons: List<TeamToggleCheckButtonView> =
            listOf(
                binding.ttcbvEditProfileTeamKt,
                binding.ttcbvEditProfileTeamLg,
                binding.ttcbvEditProfileTeamNc,
                binding.ttcbvEditProfileTeamKia,
                binding.ttcbvEditProfileTeamSsg,
                binding.ttcbvEditProfileTeamDoosan,
                binding.ttcbvEditProfileTeamHanwha,
                binding.ttcbvEditProfileTeamKiwoom,
                binding.ttcbvEditProfileTeamLotte,
                binding.ttcbvEditProfileTeamSamsung,
            )

        teamToggleCheckButtons.forEach { btn ->
            btn.binding.toggleTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.cbTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.toggleTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterEditProfileTeam.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterEditProfileTeam.btnFooterOne.isEnabled = false
                }
            }

            btn.binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.toggleTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.cbTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterEditProfileTeam.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterEditProfileTeam.btnFooterOne.isEnabled = false
                }
            }

            if (btn.binding.tvTeamToggleCheckButton.text == ClubUtils.convertClubIdToName(defaultClub)) {
                btn.binding.toggleTeamToggleCheckButton.isChecked = true
            }

            btn.setOnClickListener {
                btn.binding.toggleTeamToggleCheckButton.isChecked = !btn.binding.toggleTeamToggleCheckButton.isChecked
            }
        }
    }

    private fun initFooter() {
        binding.layoutFooterEditProfileTeam.btnFooterOne.apply {
            text = getString(R.string.complete)
            setOnClickListener {
                listener.onTeamSelected(
                    convertClubNameToId(
                        selectedButton
                            ?.binding
                            ?.tvTeamToggleCheckButton
                            ?.text
                            .toString(),
                    ),
                )
                dismiss()
            }
        }
    }
}
