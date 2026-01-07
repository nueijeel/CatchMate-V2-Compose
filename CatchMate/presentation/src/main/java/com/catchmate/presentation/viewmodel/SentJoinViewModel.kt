package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.enroll.GetRequestedEnrollListResponse
import com.catchmate.domain.usecase.enroll.GetRequestedEnrollListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentJoinViewModel
    @Inject
    constructor(
        private val getRequestedEnrollListUseCase: GetRequestedEnrollListUseCase,
    ) : ViewModel() {
        private val _requestedEnrollList = MutableLiveData<GetRequestedEnrollListResponse>()
        val requestedEnrollList: LiveData<GetRequestedEnrollListResponse>
            get() = _requestedEnrollList

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getRequestedEnrollList(page: Int) {
            viewModelScope.launch {
                val result = getRequestedEnrollListUseCase.getRequestedEnrollList(page)
                result
                    .onSuccess { response ->
                        _requestedEnrollList.value = response
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
