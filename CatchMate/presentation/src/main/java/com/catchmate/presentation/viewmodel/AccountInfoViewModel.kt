package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.auth.DeleteLogoutResponse
import com.catchmate.domain.model.user.DeleteUserAccountResponse
import com.catchmate.domain.usecase.auth.DeleteAuthLogoutUseCase
import com.catchmate.domain.usecase.user.DeleteUserAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel
    @Inject
    constructor(
        private val deleteAuthLogoutUseCase: DeleteAuthLogoutUseCase,
        private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    ) : ViewModel() {
        private val _logoutResponse = MutableLiveData<DeleteLogoutResponse>()
        val logoutResponse: LiveData<DeleteLogoutResponse>
            get() = _logoutResponse

        private val _withdrawResponse = MutableLiveData<DeleteUserAccountResponse>()
        val withdrawResponse: LiveData<DeleteUserAccountResponse>
            get() = _withdrawResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun logout(refreshToken: String) {
            viewModelScope.launch {
                val result = deleteAuthLogoutUseCase.deleteAuthLogout(refreshToken)
                result
                    .onSuccess { response ->
                        _logoutResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun withdraw() {
            viewModelScope.launch {
                val result = deleteUserAccountUseCase()
                result
                    .onSuccess { response ->
                        _withdrawResponse.value = response
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
