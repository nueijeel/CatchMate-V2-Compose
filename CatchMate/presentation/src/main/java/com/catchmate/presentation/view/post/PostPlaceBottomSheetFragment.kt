package com.catchmate.presentation.view.post

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentPostPlaceBottomSheetBinding
import com.catchmate.presentation.interaction.OnPlaceSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PostPlaceBottomSheetFragment(
    val homeTeamName: String,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentPostPlaceBottomSheetBinding? = null
    val binding get() = _binding!!

    private var selectedPlace: String = ""
    private var placeSelectedListener: OnPlaceSelectedListener? = null

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
        _binding = FragmentPostPlaceBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTextView()
        initCheckBox()
        initFooter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTextView() {
        when (homeTeamName) {
            "자이언츠" -> {
                binding.tvPostPlaceFirst.text = getString(R.string.post_place_lotte_first)
                binding.tvPostPlaceSecond.text = getString(R.string.post_place_lotte_second)
            }

            "이글스" -> {
                binding.tvPostPlaceFirst.text = getString(R.string.post_place_hanwha_first)
                binding.tvPostPlaceSecond.text = getString(R.string.post_place_hanwha_second)
            }

            "라이온즈" -> {
                binding.tvPostPlaceFirst.text = getString(R.string.post_place_samsung_first)
                binding.tvPostPlaceSecond.text = getString(R.string.post_place_samsung_second)
            }
        }

        binding.layoutPostPlaceFirst.setOnClickListener {
            binding.cbPostPlaceFirst.isChecked = !binding.cbPostPlaceFirst.isChecked
        }

        binding.layoutPostPlaceSecond.setOnClickListener {
            binding.cbPostPlaceSecond.isChecked = !binding.cbPostPlaceSecond.isChecked
        }
    }

    private fun initCheckBox() {
        binding.apply {
            cbPostPlaceFirst.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbPostPlaceSecond.isChecked = false
                    selectedPlace = tvPostPlaceFirst.text.toString()
                    layoutPostPlaceFooter.btnFooterOne.isEnabled = true
                } else {
                    selectedPlace = ""
                    layoutPostPlaceFooter.btnFooterOne.isEnabled = false
                }
            }

            cbPostPlaceSecond.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbPostPlaceFirst.isChecked = false
                    selectedPlace = tvPostPlaceSecond.text.toString()
                    layoutPostPlaceFooter.btnFooterOne.isEnabled = true
                } else {
                    selectedPlace = ""
                    layoutPostPlaceFooter.btnFooterOne.isEnabled = false
                }
            }
        }
    }

    private fun initFooter() {
        binding.layoutPostPlaceFooter.btnFooterOne.apply {
            setText(R.string.complete)
            setOnClickListener {
                placeSelectedListener?.onPlaceSelected(selectedPlace)
                dismiss()
            }
        }
    }

    fun setOnPlaceSelectedListener(listener: OnPlaceSelectedListener) {
        placeSelectedListener = listener
    }
}
