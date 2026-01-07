package com.catchmate.presentation.view.post

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentPostDateTimeBottomSheetBinding
import com.catchmate.presentation.interaction.OnDateTimeSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class PostDateTimeBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentPostDateTimeBottomSheetBinding? = null
    val binding get() = _binding!!

    private var dateTimeSelectedListener: OnDateTimeSelectedListener? = null
    private var selectedDate: String = ""

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
        _binding = FragmentPostDateTimeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTimeChipGroup()
        initFooter()
        initCalendarView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTimeChipGroup() {
        binding.cgPostDateTime.setOnCheckedStateChangeListener { group, checkedIds ->
            binding.layoutFooterPostDateTime.btnFooterOne.isEnabled = checkedIds.isNotEmpty()
        }
    }

    private fun initCalendarView() {
        binding.cvPostDateTime.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            Log.i("selectedDate", selectedDate)
        }
    }

    private fun initFooter() {
        binding.layoutFooterPostDateTime.btnFooterOne.apply {
            text = getString(R.string.complete)
            setOnClickListener {
                val checkedChipId = binding.cgPostDateTime.checkedChipId
                if (checkedChipId == View.NO_ID) {
                    return@setOnClickListener
                }
                val checkedChip = binding.root.findViewById<Chip>(checkedChipId)
                dateTimeSelectedListener?.onDateTimeSelected(
                    selectedDate,
                    checkedChip.text.toString(),
                )
                dismiss()
            }
        }
    }

    fun setOnDateTimeSelectedListener(listener: OnDateTimeSelectedListener) {
        dateTimeSelectedListener = listener
    }
}
