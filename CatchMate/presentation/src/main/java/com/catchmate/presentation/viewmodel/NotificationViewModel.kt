package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.notification.DeleteReceivedNotificationResponse
import com.catchmate.domain.model.notification.GetReceivedNotificationListResponse
import com.catchmate.domain.model.notification.GetReceivedNotificationResponse
import com.catchmate.domain.usecase.notification.DeleteReceivedNotificationUseCase
import com.catchmate.domain.usecase.notification.GetReceivedNotificationListUseCase
import com.catchmate.domain.usecase.notification.GetReceivedNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        private val getReceivedNotificationListUseCase: GetReceivedNotificationListUseCase,
        private val getReceivedNotificationUseCase: GetReceivedNotificationUseCase,
        private val deleteReceivedNotificationUseCase: DeleteReceivedNotificationUseCase,
    ) : ViewModel() {
        private val _receivedNotificationList = MutableLiveData<GetReceivedNotificationListResponse>()
        val receivedNotificationList: LiveData<GetReceivedNotificationListResponse>
            get() = _receivedNotificationList

        private val _receivedNotification = MutableLiveData<GetReceivedNotificationResponse>()
        val receivedNotification: LiveData<GetReceivedNotificationResponse>
            get() = _receivedNotification

        private val _deletedNotificationResponse = MutableLiveData<DeleteReceivedNotificationResponse>()
        val deletedNotificationResponse: LiveData<DeleteReceivedNotificationResponse>
            get() = _deletedNotificationResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getReceivedNotificationList(page: Int) {
            viewModelScope.launch {
                val result = getReceivedNotificationListUseCase.getReceivedNotificationList(page)
                result
                    .onSuccess { response ->
                        _receivedNotificationList.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getReceivedNotification(notificationId: Long) {
            viewModelScope.launch {
                val result = getReceivedNotificationUseCase.getReceivedNotification(notificationId)
                result
                    .onSuccess { response ->
                        _receivedNotification.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun deleteNotification(notificationId: Long) {
            viewModelScope.launch {
                val result = deleteReceivedNotificationUseCase.deleteReceivedNotification(notificationId)
                result
                    .onSuccess { response ->
                        _deletedNotificationResponse.value = response
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
