package com.catchmate.presentation.view.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.databinding.FragmentHomeHeadCountFilterBottomSheetBinding
import com.catchmate.presentation.interaction.OnPersonFilterSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeHeadCountFilterBottomSheetFragment(
    private val defaultCount: Int?,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentHomeHeadCountFilterBottomSheetBinding? = null
    val binding get() = _binding!!

    private var listener: OnPersonFilterSelectedListener? = null
    private var selectedCount: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val behavior = dialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }

    private val headCountArray =
        arrayOf(
            "1명",
            "2명",
            "3명",
            "4명",
            "5명",
            "6명",
            "7명",
            "8명",
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeHeadCountFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initNumberPicker()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initNumberPicker() {
        binding.npHomeHeadCountFilter.run {
            wrapSelectorWheel = false
            minValue = 0
            maxValue = headCountArray.size - 1
            displayedValues = headCountArray
            value = defaultCount?.minus(1) ?: 0
        }
        binding.npHomeHeadCountFilter.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedCount = newVal + 1
        }
    }

    private fun initFooter() {
        binding.layoutHomeHeadCountFilterFooter.apply {
            btnFilterFooterApply.setOnClickListener {
                listener?.onPersonFilterSelected(selectedCount)
                dismiss()
            }
            // 초기화 시 그냥 인원값 null로 보내고 바텀시트 닫음
            btnFilterFooterReset.setOnClickListener {
                listener?.onPersonFilterSelected(null)
                dismiss()
            }
        }
    }

    fun setOnPersonFilterSelected(listener: OnPersonFilterSelectedListener) {
        this.listener = listener
    }
}
