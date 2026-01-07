package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.support.GetInquiryResponse
import com.catchmate.domain.usecase.support.GetInquiryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceCenterAnswerViewModel
    @Inject
    constructor(
        private val getInquiryUseCase: GetInquiryUseCase,
    ) : ViewModel() {
        private val _getInquiryResponse = MutableLiveData<GetInquiryResponse>()
        val getInquiryResponse: LiveData<GetInquiryResponse>
            get() = _getInquiryResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getInquiry(inquiryId: Long) {
            viewModelScope.launch {
                val result = getInquiryUseCase(inquiryId)
                result
                    .onSuccess { response ->
                        _getInquiryResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }
    }
