package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.support.PostUserReportRequest
import com.catchmate.domain.model.support.PostUserReportResponse
import com.catchmate.domain.usecase.support.PostUserReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
    @Inject
    constructor(
        private val postUserReportUseCase: PostUserReportUseCase,
    ) : ViewModel() {
        private val _postUserReportResponse = MutableLiveData<PostUserReportResponse>()
        val postUserReportResponse: LiveData<PostUserReportResponse>
            get() = _postUserReportResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun postUserReport(
            reportedUserId: Long,
            request: PostUserReportRequest,
        ) {
            viewModelScope.launch {
                val result = postUserReportUseCase(reportedUserId, request)
                result
                    .onSuccess { response ->
                        _postUserReportResponse.value = response
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
