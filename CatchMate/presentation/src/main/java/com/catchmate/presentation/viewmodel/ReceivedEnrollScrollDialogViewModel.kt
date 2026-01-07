package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.enroll.GetReceivedEnrollResponse
import com.catchmate.domain.model.enroll.PatchEnrollAcceptResponse
import com.catchmate.domain.model.enroll.PatchEnrollRejectResponse
import com.catchmate.domain.usecase.enroll.GetReceivedEnrollUseCase
import com.catchmate.domain.usecase.enroll.PatchEnrollAcceptUseCase
import com.catchmate.domain.usecase.enroll.PatchEnrollRejectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceivedEnrollScrollDialogViewModel
    @Inject
    constructor(
        private val getReceivedEnrollUseCase: GetReceivedEnrollUseCase,
        private val patchEnrollRejectUseCase: PatchEnrollRejectUseCase,
        private val patchEnrollAcceptUseCase: PatchEnrollAcceptUseCase,
    ) : ViewModel() {
        private val _getReceivedEnrollResponse = MutableLiveData<GetReceivedEnrollResponse>()
        val getReceivedEnrollResponse: LiveData<GetReceivedEnrollResponse>
            get() = _getReceivedEnrollResponse

        private val _patchEnrollReject = MutableLiveData<PatchEnrollRejectResponse>()
        val patchEnrollReject: LiveData<PatchEnrollRejectResponse>
            get() = _patchEnrollReject

        private val _patchEnrollAccept = MutableLiveData<PatchEnrollAcceptResponse>()
        val patchEnrollAccept: LiveData<PatchEnrollAcceptResponse>
            get() = _patchEnrollAccept

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getReceivedEnroll(boardId: Long) {
            viewModelScope.launch {
                val result = getReceivedEnrollUseCase.getReceivedEnroll(boardId)
                result
                    .onSuccess { response ->
                        _getReceivedEnrollResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun patchEnrollReject(enrollId: Long) {
            viewModelScope.launch {
                val result = patchEnrollRejectUseCase.patchEnrollReject(enrollId)
                result
                    .onSuccess { response ->
                        _patchEnrollReject.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun patchEnrollAccept(enrollId: Long) {
            viewModelScope.launch {
                val result = patchEnrollAcceptUseCase.patchEnrollAccept(enrollId)
                result
                    .onSuccess { response ->
                        _patchEnrollAccept.value = response
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
