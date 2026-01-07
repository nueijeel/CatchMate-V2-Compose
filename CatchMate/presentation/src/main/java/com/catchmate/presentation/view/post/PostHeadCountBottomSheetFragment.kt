package com.catchmate.presentation.view.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentPostHeadCountBottomSheetBinding
import com.catchmate.presentation.interaction.OnPeopleCountSelectedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class PostHeadCountBottomSheetFragment(
    private val maxPerson: Int? = null,
    private val currentPerson: Int? = null,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentPostHeadCountBottomSheetBinding? = null
    val binding get() = _binding!!

    private var peopleCountSelectedListener: OnPeopleCountSelectedListener? = null
    private var selectedValueIdx = if (maxPerson == null) 0 else maxPerson - 2

    private val headCountArray =
        arrayOf(
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
        _binding = FragmentPostHeadCountBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initFooterButton()
        initNumberPicker()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnPeopleCountSelectedListener(listener: OnPeopleCountSelectedListener) {
        peopleCountSelectedListener = listener
    }

    private fun initFooterButton() {
        binding.layoutPostHeadCountFooter.btnFooterOne.run {
            isEnabled = true
            setText(R.string.complete)
            setOnClickListener {
                if (currentPerson != null && maxPerson != null) {
                    peopleCountSelectedListener?.onPeopleCountSelected(selectedValueIdx + 2)
                } else {
                    peopleCountSelectedListener?.onPeopleCountSelected(binding.npPostHeadCount.value + 2)
                }
                dismiss()
            }
        }
    }

    private fun initNumberPicker() {
        binding.npPostHeadCount.run {
            wrapSelectorWheel = false
            minValue = 0
            maxValue = headCountArray.size - 1
            displayedValues = headCountArray
            value = selectedValueIdx
            if (currentPerson != null && maxPerson != null) {
                setOnValueChangedListener { picker, oldVal, newVal ->
                    Log.i("value changed", "$oldVal - $newVal")
                    if (newVal < currentPerson - 2) {
                        Snackbar.make(requireView(), R.string.post_edt_number_picker_invalid_value_toast, Snackbar.LENGTH_SHORT).show()
                        Thread.sleep(400)
                        picker.value = maxPerson - 2
                    } else {
                        selectedValueIdx = newVal
                    }
                }
            }
        }
    }
}
