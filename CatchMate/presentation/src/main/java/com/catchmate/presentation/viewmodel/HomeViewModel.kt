package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.board.GetBoardListResponse
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.domain.usecase.board.GetBoardListUseCase
import com.catchmate.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getUserProfileUseCase: GetUserProfileUseCase,
        private val getBoardListUseCase: GetBoardListUseCase,
    ) : ViewModel() {
        private val _userProfile = MutableLiveData<GetUserProfileResponse>()
        val userProfile: LiveData<GetUserProfileResponse>
            get() = _userProfile

        private val _getBoardListResponse = MutableLiveData<GetBoardListResponse>()
        val getBoardListResponse: LiveData<GetBoardListResponse>
            get() = _getBoardListResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun getUserProfile() {
            viewModelScope.launch {
                val result = getUserProfileUseCase.getUserProfile()
                result
                    .onSuccess { user ->
                        _userProfile.value = user
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getBoardList(
            gameStartDate: String? = null,
            maxPerson: Int? = null,
            preferredTeamIdList: Array<Int>? = null,
            page: Int? = null,
        ) {
            viewModelScope.launch {
                val result = getBoardListUseCase.getBoardList(gameStartDate, maxPerson, preferredTeamIdList, page)
                result
                    .onSuccess { boardList ->
                        _getBoardListResponse.value = boardList
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = "ListLoadError"
                        }
                    }
            }
        }
    }
