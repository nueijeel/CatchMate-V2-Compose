package com.catchmate.presentation.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentPostCheerTeamBottomSheetBinding
import com.catchmate.presentation.interaction.OnCheerTeamSelectedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostCheerTeamBottomSheetFragment(
    val homeTeam: String,
    val awayTeam: String,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentPostCheerTeamBottomSheetBinding? = null
    val binding get() = _binding!!

    private var selectedButton: TeamToggleCheckButtonView? = null
    private var cheerTeamSelectedListener: OnCheerTeamSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPostCheerTeamBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTeamToggleCheckButtonResources(homeTeam, binding.ttcbvPostCheerTeamHome)
        initTeamToggleCheckButtonResources(awayTeam, binding.ttcbvPostCheerTeamAway)
        initTeamButton()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTeamButton() {
        val teamButtons: List<TeamToggleCheckButtonView> =
            listOf(
                binding.ttcbvPostCheerTeamHome,
                binding.ttcbvPostCheerTeamAway,
            )

        teamButtons.forEach { btn ->
            btn.binding.toggleTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.cbTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.toggleTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterPostCheerTeam.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterPostCheerTeam.btnFooterOne.isEnabled = false
                }
            }

            btn.binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.toggleTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.cbTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterPostCheerTeam.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterPostCheerTeam.btnFooterOne.isEnabled = false
                }
            }

            btn.setOnClickListener {
                btn.binding.toggleTeamToggleCheckButton.isChecked = !btn.binding.toggleTeamToggleCheckButton.isChecked
            }
        }
    }

    private fun initFooter() {
        binding.layoutFooterPostCheerTeam.btnFooterOne.apply {
            text = getString(R.string.complete)
            setOnClickListener {
                cheerTeamSelectedListener?.onCheerTeamSelected(
                    selectedButton
                        ?.binding
                        ?.tvTeamToggleCheckButton
                        ?.text
                        .toString(),
                )
                dismiss()
            }
        }
    }

    fun setOnCheerTeamSelectedListener(listener: OnCheerTeamSelectedListener) {
        cheerTeamSelectedListener = listener
    }

    private fun initTeamToggleCheckButtonResources(
        teamName: String,
        buttonView: TeamToggleCheckButtonView,
    ) {
        buttonView.binding.tvTeamToggleCheckButton.text = teamName
        when (teamName) {
            "다이노스" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_nc_dinos_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_nc_dinos_logo)
                }
            }

            "라이온즈" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_samsung_lions_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_samsung_lions_logo)
                }
            }

            "랜더스" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_ssg_landers_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_ssg_landers_logo)
                }
            }

            "베어스" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_doosan_bears_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_doosan_bears_logo)
                }
            }

            "위즈" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_kt_wiz_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_kt_wiz_logo)
                }
            }

            "이글스" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_hanwha_eagles_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_hanwha_eagles_logo)
                }
            }

            "자이언츠" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_lotte_giants_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_lotte_giants_logo)
                }
            }

            "타이거즈" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_kia_tigers_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_kia_tigers_logo)
                }
            }

            "트윈스" -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_lg_twins_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_lg_twins_logo)
                }
            }

            else -> {
                buttonView.binding.apply {
                    toggleTeamToggleCheckButton.setBackgroundResource(R.drawable.selector_kiwoom_heroes_bg)
                    ivTeamToggleCheckButton.setImageResource(R.drawable.vec_all_kiwoom_heroes_logo)
                }
            }
        }
    }
}
