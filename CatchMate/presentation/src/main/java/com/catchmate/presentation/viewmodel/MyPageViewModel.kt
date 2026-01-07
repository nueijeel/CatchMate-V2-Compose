package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.enroll.GetEnrollNewCountResponse
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.domain.usecase.enroll.GetEnrollNewCountUseCase
import com.catchmate.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        private val getUserProfileUseCase: GetUserProfileUseCase,
        private val getEnrollNewCountUseCase: GetEnrollNewCountUseCase,
    ) : ViewModel() {
        private val _userProfile = MutableLiveData<GetUserProfileResponse>()
        val userProfile: LiveData<GetUserProfileResponse>
            get() = _userProfile

        private val _newCount = MutableLiveData<GetEnrollNewCountResponse>()
        val newCount: LiveData<GetEnrollNewCountResponse>
            get() = _newCount

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getUserProfile() {
            viewModelScope.launch {
                val result = getUserProfileUseCase.getUserProfile()
                result
                    .onSuccess { response ->
                        _userProfile.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getEnrollNewCount() {
            viewModelScope.launch {
                val result = getEnrollNewCountUseCase.getEnrollNewCount()
                result
                    .onSuccess { response ->
                        _newCount.value = response
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
