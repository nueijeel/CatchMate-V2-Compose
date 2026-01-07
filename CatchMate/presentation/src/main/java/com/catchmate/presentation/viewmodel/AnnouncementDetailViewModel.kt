package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.domain.usecase.support.GetNoticeDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementDetailViewModel
    @Inject
    constructor(
        private val getNoticeDetailUseCase: GetNoticeDetailUseCase,
    ) : ViewModel() {
        private val _getNoticeDetailResponse = MutableLiveData<NoticeInfo>()
        val getNoticeDetailResponse: LiveData<NoticeInfo>
            get() = _getNoticeDetailResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getNoticeDetail(noticeId: Long) {
            viewModelScope.launch {
                val result = getNoticeDetailUseCase(noticeId)
                result
                    .onSuccess { response ->
                        _getNoticeDetailResponse.value = response
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
