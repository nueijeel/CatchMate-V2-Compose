package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.user.PatchUserAlarmResponse
import com.catchmate.domain.usecase.user.PatchUserAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel
    @Inject
    constructor(
        private val patchUserAlarmUseCase: PatchUserAlarmUseCase,
    ) : ViewModel() {
        private val _patchUserAlarmResponse = MutableLiveData<PatchUserAlarmResponse>()
        val patchUserAlarmResponse: LiveData<PatchUserAlarmResponse>
            get() = _patchUserAlarmResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun patchUserAlarm(
            alarmType: String,
            isEnabled: String,
        ) {
            viewModelScope.launch {
                val result = patchUserAlarmUseCase.patchUserAlarm(alarmType, isEnabled)
                result
                    .onSuccess { response ->
                        _patchUserAlarmResponse.value = response
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
