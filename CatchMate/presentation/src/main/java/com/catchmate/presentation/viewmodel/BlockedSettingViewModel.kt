package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.user.DeleteBlockedUserResponse
import com.catchmate.domain.model.user.GetBlockedUserListResponse
import com.catchmate.domain.usecase.user.DeleteBlockedUserUseCase
import com.catchmate.domain.usecase.user.GetBlockedUserListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedSettingViewModel
    @Inject
    constructor(
        private val getBlockedUserListUseCase: GetBlockedUserListUseCase,
        private val deleteBlockedUserUseCase: DeleteBlockedUserUseCase,
    ) : ViewModel() {
        private val _getBlockedUserListResponse = MutableLiveData<GetBlockedUserListResponse>()
        val getBlockedUserListResponse: LiveData<GetBlockedUserListResponse>
            get() = _getBlockedUserListResponse

        private val _deleteBlockedUserResponse = MutableLiveData<DeleteBlockedUserResponse>()
        val deleteBlockedUserResponse: LiveData<DeleteBlockedUserResponse>
            get() = _deleteBlockedUserResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun deleteUserFromList(userId: Long) {
            val userList = _getBlockedUserListResponse.value?.userInfoList!!
            val newUserList = userList.filter { it.userId != userId }.toMutableList()

            val updatedResponse =
                _getBlockedUserListResponse.value?.copy(
                    userInfoList = newUserList,
                ) ?: GetBlockedUserListResponse(
                    userInfoList = newUserList,
                    totalPages = 1,
                    totalElements = 1,
                    isFirst = true,
                    isLast = true,
                )

            _getBlockedUserListResponse.postValue(updatedResponse)
        }

        fun getBlockedUserList(page: Int) {
            viewModelScope.launch {
                val result = getBlockedUserListUseCase(page)
                result
                    .onSuccess { response ->
                        _getBlockedUserListResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun deleteBlockedUser(blockedUserId: Long) {
            viewModelScope.launch {
                val result = deleteBlockedUserUseCase(blockedUserId)
                result
                    .onSuccess { response ->
                        _deleteBlockedUserResponse.value = response
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
