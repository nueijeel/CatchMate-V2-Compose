package com.catchmate.presentation.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.catchmate.presentation.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : Fragment() {
    private var _binding: VB? = null
    val binding
        get() = requireNotNull(_binding)

    private var backPressedCallback: OnBackPressedCallback? = null
    protected var onBackPressedAction: (() -> Unit)? = null
    protected var enableDoubleBackPressedExit = false
    private var lastBackPressedTime: Long = 0L // 마지막으로 백버튼 눌렀던 시간 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        backPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (enableDoubleBackPressedExit) {
                        handleDoubleBackToExit()
                    } else {
                        onBackPressedAction?.invoke() ?: findNavController().popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback as OnBackPressedCallback,
        )
    }

    private fun handleDoubleBackToExit() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime < 2000) {
            requireActivity().moveTaskToBack(true) // 앱을 백그라운드로 이동시킴
        } else {
            lastBackPressedTime = currentTime
            Snackbar.make(requireView(), R.string.back_pressed_toast, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        backPressedCallback?.remove()
    }
}
