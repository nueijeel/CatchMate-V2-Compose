package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.support.GetNoticeListResponse
import com.catchmate.domain.usecase.support.GetNoticeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel
    @Inject
    constructor(
        private val getNoticeListUseCase: GetNoticeListUseCase,
    ) : ViewModel() {
        private val _getNoticeListResponse = MutableLiveData<GetNoticeListResponse>()
        val getNoticeListResponse: LiveData<GetNoticeListResponse>
            get() = _getNoticeListResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getNoticeList(page: Int) {
            viewModelScope.launch {
                val result = getNoticeListUseCase(page)
                result
                    .onSuccess { response ->
                        _getNoticeListResponse.value = response
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
