package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.enroll.GetAllReceivedEnrollResponse
import com.catchmate.domain.usecase.enroll.GetAllReceivedEnrollUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceivedJoinViewModel
    @Inject
    constructor(
        private val getAllReceivedEnrollUseCase: GetAllReceivedEnrollUseCase,
    ) : ViewModel() {
        private var _getAllReceivedEnrollResponse = MutableLiveData<GetAllReceivedEnrollResponse>()
        val getAllReceivedEnrollResponse: LiveData<GetAllReceivedEnrollResponse>
            get() = _getAllReceivedEnrollResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getAllReceivedEnroll(page: Int) {
            viewModelScope.launch {
                val result = getAllReceivedEnrollUseCase.getAllReceivedEnroll(page)
                result
                    .onSuccess { response ->
                        _getAllReceivedEnrollResponse.value = response
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
