package com.catchmate.presentation.view.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.databinding.FragmentHomeDateFilterBottomSheetBinding
import com.catchmate.presentation.interaction.OnDateFilterSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class HomeDateFilterBottomSheetFragment(
    val default: String?,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentHomeDateFilterBottomSheetBinding? = null
    val binding get() = _binding!!

    private var dateFilterSelectedListener: OnDateFilterSelectedListener? = null
    private var selectedDate: String? = null

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
        _binding = FragmentHomeDateFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // 이전에 선택했던 필터값 적용
        selectedDate = default
        if (default != null) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val parsedDate = inputDateFormat.parse(default)
            binding.cvHomeDateFilter.date = parsedDate.time
        }
        initCalendarView()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initCalendarView() {
        binding.cvHomeDateFilter.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            Log.i("DATE", selectedDate!!)
        }
    }

    private fun initFooter() {
        binding.layoutFooterHomeDateFilter.apply {
            btnFilterFooterReset.setOnClickListener {
                // 날짜표시 아예 없앨 수 없어서 현재 날짜로 표시만되는 로직, 선택 값은 null로 초기화 됨
                val currentDate = LocalDate.now()
                val epochMilli = currentDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
                binding.cvHomeDateFilter.date = epochMilli
                selectedDate = null
            }
            btnFilterFooterApply.setOnClickListener {
                dateFilterSelectedListener?.onDateSelected(selectedDate)
                dismiss()
            }
        }
    }

    fun setOnDateFilterSelectedListener(listener: OnDateFilterSelectedListener) {
        dateFilterSelectedListener = listener
    }
}
