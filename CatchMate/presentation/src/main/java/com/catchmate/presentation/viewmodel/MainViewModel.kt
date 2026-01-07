package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.user.GetUnreadInfoResponse
import com.catchmate.domain.usecase.user.GetUnreadInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val getUnreadInfoUseCase: GetUnreadInfoUseCase,
    ) : ViewModel() {
        private val _getUnreadInfoResponse = MutableLiveData<GetUnreadInfoResponse>()
        val getUnreadInfoResponse: LiveData<GetUnreadInfoResponse>
            get() = _getUnreadInfoResponse

        private val _isGuestLogin = MutableLiveData<Boolean>()
        val isGuestLogin: LiveData<Boolean> get() = _isGuestLogin

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun setGuestLogin(isGuest: Boolean) {
            _isGuestLogin.value = isGuest
        }

        fun getUnreadInfo() {
            viewModelScope.launch {
                val result = getUnreadInfoUseCase()
                result
                    .onSuccess { unreadInfo ->
                        _getUnreadInfoResponse.value = unreadInfo
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
