package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.support.PostInquiryRequest
import com.catchmate.domain.model.support.PostInquiryResponse
import com.catchmate.domain.usecase.support.PostInquiryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceCenterViewModel
    @Inject
    constructor(
        private val postInquiryUseCase: PostInquiryUseCase,
    ) : ViewModel() {
        private val _postInquiryResponse = MutableLiveData<PostInquiryResponse>()
        val postInquiryResponse: LiveData<PostInquiryResponse>
            get() = _postInquiryResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun postInquiry(request: PostInquiryRequest) {
            viewModelScope.launch {
                val result = postInquiryUseCase(request)
                result
                    .onSuccess { response ->
                        _postInquiryResponse.value = response
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
