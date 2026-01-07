package com.catchmate.presentation.view.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.databinding.FragmentHomeTeamFilterBottomSheetBinding
import com.catchmate.presentation.interaction.OnClubFilterSelectedListener
import com.catchmate.presentation.util.ClubUtils.convertClubIdToName
import com.catchmate.presentation.util.ClubUtils.convertClubNameToId
import com.catchmate.presentation.view.post.TeamToggleCheckButtonView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeTeamFilterBottomSheetFragment(
    private val defaultClubList: Array<Int>?,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentHomeTeamFilterBottomSheetBinding? = null
    val binding get() = _binding!!

    private var clubSelectedListener: OnClubFilterSelectedListener? = null
    private var selectedButton: MutableList<TeamToggleCheckButtonView> = mutableListOf()

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
        _binding = FragmentHomeTeamFilterBottomSheetBinding.inflate(inflater, container, false)
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
                binding.ttcbvTeamFilterKt,
                binding.ttcbvTeamFilterLg,
                binding.ttcbvTeamFilterNc,
                binding.ttcbvTeamFilterLotte,
                binding.ttcbvTeamFilterKia,
                binding.ttcbvTeamFilterSsg,
                binding.ttcbvTeamFilterDoosan,
                binding.ttcbvTeamFilterHanwha,
                binding.ttcbvTeamFilterKiwoom,
                binding.ttcbvTeamFilterSamsung,
            )

        teamToggleCheckButtons.forEach { btn ->
            var isSyncing = false

            // 토글 버튼 체크 변경 이벤트
            btn.binding.toggleTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isSyncing) return@setOnCheckedChangeListener // 상호 호출 방지
                isSyncing = true

                btn.binding.cbTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton.add(btn)
                } else {
                    selectedButton.remove(btn)
                }

                isSyncing = false
            }

            // 체크 버튼 체크 변경 이벤트
            btn.binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isSyncing) return@setOnCheckedChangeListener // 상호 호출 방지
                isSyncing = true

                btn.binding.toggleTeamToggleCheckButton.isChecked = isChecked
                if (isChecked) {
                    selectedButton.add(btn)
                } else {
                    selectedButton.remove(btn)
                }
                isSyncing = false
            }

            // 이미 선택된 구단 있을 때 체크 상태 셋팅
            defaultClubList?.forEach { id ->
                if (convertClubIdToName(id) == btn.binding.tvTeamToggleCheckButton.text) {
                    btn.binding.toggleTeamToggleCheckButton.isChecked = true
                }
            }

            // 버튼 뷰 전체 클릭 범위 설정
            btn.setOnClickListener {
                btn.binding.toggleTeamToggleCheckButton.isChecked = !btn.binding.toggleTeamToggleCheckButton.isChecked
            }
        }
    }

    private fun initFooter() {
        binding.layoutTeamFilterFooter.apply {
            btnFilterFooterReset.setOnClickListener {
                if (selectedButton.isNotEmpty()) {
                    selectedButton.toList().forEach { btn ->
                        btn.binding.toggleTeamToggleCheckButton.isChecked = false
                    }
                    selectedButton.clear()
                }
            }
            btnFilterFooterApply.setOnClickListener {
                if (selectedButton.isEmpty()) {
                    clubSelectedListener?.onClubFilterSelected(null)
                } else {
                    val clubList = mutableListOf<Int>()
                    selectedButton.forEach { btn ->
                        clubList.add(
                            convertClubNameToId(
                                btn
                                    .binding
                                    .tvTeamToggleCheckButton
                                    .text
                                    .toString(),
                            ),
                        )
                    }
                    clubSelectedListener?.onClubFilterSelected(clubList.toTypedArray())
                }
                dismiss()
            }
        }
    }

    fun setOnClubSelectedListener(listener: OnClubFilterSelectedListener) {
        clubSelectedListener = listener
    }
}
