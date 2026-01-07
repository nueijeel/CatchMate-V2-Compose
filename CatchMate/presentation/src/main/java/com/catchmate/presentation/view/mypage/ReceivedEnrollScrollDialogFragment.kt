package com.catchmate.presentation.view.mypage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.catchmate.presentation.databinding.FragmentReceivedEnrollScrollDialogBinding
import com.catchmate.presentation.interaction.OnReceivedEnrollResultSelectedListener
import com.catchmate.presentation.viewmodel.ReceivedEnrollScrollDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceivedEnrollScrollDialogFragment :
    DialogFragment(),
    OnReceivedEnrollResultSelectedListener {
    private var _binding: FragmentReceivedEnrollScrollDialogBinding? = null
    val binding get() = _binding!!

    private val receivedEnrollScrollDialogViewModel: ReceivedEnrollScrollDialogViewModel by viewModels()
    private var boardId: Long = 0L

    companion object {
        private const val ARG_ITEM = "boardId"

        fun newInstance(item: Long): ReceivedEnrollScrollDialogFragment {
            val fragment = ReceivedEnrollScrollDialogFragment()
            val bundle = Bundle()
            bundle.putLong(ARG_ITEM, item)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            boardId = it.getLong(ARG_ITEM)
            Log.i(ARG_ITEM, boardId.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentReceivedEnrollScrollDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        binding.rvReceivedEnrollScrollDialog.apply {
            adapter = ReceivedEnrollAdapter(requireContext(), layoutInflater, this@ReceivedEnrollScrollDialogFragment)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvReceivedEnrollScrollDialog)
    }

    private fun setData() {
        receivedEnrollScrollDialogViewModel.getReceivedEnroll(boardId)
        receivedEnrollScrollDialogViewModel.getReceivedEnrollResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val adapter = binding.rvReceivedEnrollScrollDialog.adapter as ReceivedEnrollAdapter
                adapter.updateEnrollList(response.enrollInfoList.first().enrollReceiveInfoList)
            }
        }
    }

    override fun onReceivedEnrollRejected(enrollId: Long) {
        receivedEnrollScrollDialogViewModel.patchEnrollReject(enrollId)
        receivedEnrollScrollDialogViewModel.patchEnrollReject.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("STATUS", response.acceptStatus)
                dismiss()
            }
        }
    }

    override fun onReceivedEnrollAccepted(enrollId: Long) {
        receivedEnrollScrollDialogViewModel.patchEnrollAccept(enrollId)
        receivedEnrollScrollDialogViewModel.patchEnrollAccept.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.i("STATUS", response.acceptStatus)
                dismiss()
            }
        }
    }
}
