package com.catchmate.presentation.view.post

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentPostPlayTeamBottomSheetBinding
import com.catchmate.presentation.interaction.OnTeamSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostPlayTeamBottomSheetFragment(
    val firstTeam: String?,
    val secondTeam: String?,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentPostPlayTeamBottomSheetBinding? = null
    val binding get() = _binding!!

    var teamSelectedListener: OnTeamSelectedListener? = null
    var selectedButton: TeamToggleCheckButtonView? = null
    lateinit var teamType: String

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
        _binding = FragmentPostPlayTeamBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTeamButtons()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTeamButtons() {
        val teamToggleCheckButtons: List<TeamToggleCheckButtonView> =
            listOf(
                binding.ttcbvPlayTeamBottomSheetNc,
                binding.ttcbvPlayTeamBottomSheetSsg,
                binding.ttcbvPlayTeamBottomSheetSamsung,
                binding.ttcbvPlayTeamBottomSheetDoosan,
                binding.ttcbvPlayTeamBottomSheetKt,
                binding.ttcbvPlayTeamBottomSheetHanwha,
                binding.ttcbvPlayTeamBottomSheetKia,
                binding.ttcbvPlayTeamBottomSheetLotte,
                binding.ttcbvPlayTeamBottomSheetLg,
                binding.ttcbvPlayTeamBottomSheetKiwoom,
            )

        teamToggleCheckButtons.forEach { btn ->
            // 토글 상태 변경에 따른 제어
            btn.binding.toggleTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.cbTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.toggleTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterPlayTeamBottomSheet.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterPlayTeamBottomSheet.btnFooterOne.isEnabled = false
                }
            }

            // 체크박스 상태 변경에 따른 제어
            btn.binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                btn.binding.toggleTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton?.binding?.cbTeamToggleCheckButton?.isChecked = false
                    buttonView.isChecked = true
                    selectedButton = btn
                    binding.layoutFooterPlayTeamBottomSheet.btnFooterOne.isEnabled = true
                } else {
                    binding.layoutFooterPlayTeamBottomSheet.btnFooterOne.isEnabled = false
                }
            }

            // 디폴트 버튼 설정
            if (firstTeam != null && btn.binding.tvTeamToggleCheckButton.text == firstTeam) {
                btn.binding.toggleTeamToggleCheckButton.isChecked = true
            }

            // 비활성화 버튼 설정
            if (secondTeam != null && btn.binding.tvTeamToggleCheckButton.text == secondTeam) {
                btn.isEnabled = false
                btn.binding.tvTeamToggleCheckButton.isEnabled = false
            }

            // 버튼 전체 클릭 시 체크 상태 반영되도록
            btn.setOnClickListener {
                btn.binding.toggleTeamToggleCheckButton.isChecked = !btn.binding.toggleTeamToggleCheckButton.isChecked
            }
        }
    }

    private fun initFooter() {
        binding.layoutFooterPlayTeamBottomSheet.btnFooterOne.apply {
            text = getString(R.string.complete)
            setOnClickListener {
                teamSelectedListener?.onTeamSelected(
                    selectedButton
                        ?.binding
                        ?.tvTeamToggleCheckButton
                        ?.text
                        .toString(),
                    teamType,
                )
                dismiss()
            }
        }
    }

    fun setOnTeamSelectedListener(
        listener: OnTeamSelectedListener,
        teamType: String,
    ) {
        teamSelectedListener = listener
        this.teamType = teamType
    }
}
