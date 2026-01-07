package com.catchmate.presentation.view.support

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.FragmentInformationBinding
import com.catchmate.presentation.view.base.BaseFragment

class InformationFragment : BaseFragment<FragmentInformationBinding>(FragmentInformationBinding::inflate) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            layoutHeaderInformation.apply {
                imgbtnHeaderTextBack.setOnClickListener {
                    findNavController().popBackStack()
                }
                tvHeaderTextTitle.text = getString(R.string.information_title)
            }
            tvAppVersionInformation.text = "v.1.0.0" // 추후 버전 적용
            tvLibraryHyperlinkInformation.setOnClickListener {
                // 관련 링크 걸기
            }
        }
    }
}
