package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.NonExistentTempBoardException
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.model.board.GetTempBoardResponse
import com.catchmate.domain.model.board.PatchBoardRequest
import com.catchmate.domain.model.board.PatchBoardResponse
import com.catchmate.domain.model.board.PostBoardRequest
import com.catchmate.domain.model.board.PostBoardResponse
import com.catchmate.domain.usecase.board.GetTempBoardUseCase
import com.catchmate.domain.usecase.board.PatchBoardUseCase
import com.catchmate.domain.usecase.board.PostBoardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel
    @Inject
    constructor(
        private val postBoardUseCase: PostBoardUseCase,
        private val patchBoardUseCase: PatchBoardUseCase,
        private val getTempBoardUseCase: GetTempBoardUseCase,
    ) : ViewModel() {
        private var _homeTeamName = MutableLiveData<String>()
        val homeTeamName: LiveData<String>
            get() = _homeTeamName

        private var _awayTeamName = MutableLiveData<String>()
        val awayTeamName: LiveData<String>
            get() = _awayTeamName

        private var _gameDateTime = MutableLiveData<String>()
        val gameDateTime: LiveData<String>
            get() = _gameDateTime

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        private var _postBoardResponse = MutableLiveData<PostBoardResponse>()
        val postBoardResponse: LiveData<PostBoardResponse>
            get() = _postBoardResponse

        private var _patchBoardResponse = MutableLiveData<PatchBoardResponse>()
        val patchBoardResponse: LiveData<PatchBoardResponse>
            get() = _patchBoardResponse

        private var _getTempBoardResponse = MutableLiveData<GetTempBoardResponse>()
        val getTempBoardResponse: LiveData<GetTempBoardResponse>
            get() = _getTempBoardResponse

        private val _noTempBoardMessage = MutableLiveData<String?>()
        val noTempBoardMessage: LiveData<String?>
            get() = _noTempBoardMessage

        private val _boardInfo = MutableLiveData<GetBoardResponse?>()
        val boardInfo: LiveData<GetBoardResponse?>
            get() = _boardInfo

        fun setHomeTeamName(teamName: String) {
            _homeTeamName.value = teamName
        }

        fun setAwayTeamName(teamName: String) {
            _awayTeamName.value = teamName
        }

        fun setGameDate(gameDateTime: String) {
            _gameDateTime.value = gameDateTime
        }

        fun setBoardInfo(boardInfo: GetBoardResponse?) {
            _boardInfo.value = boardInfo
        }

        fun postBoard(postBoardRequest: PostBoardRequest) {
            viewModelScope.launch {
                val result = postBoardUseCase.postBoard(postBoardRequest)
                result
                    .onSuccess { response ->
                        _postBoardResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun patchBoard(
            boardId: Long,
            patchBoardRequest: PatchBoardRequest,
        ) {
            viewModelScope.launch {
                val result = patchBoardUseCase.patchBoard(boardId, patchBoardRequest)
                result
                    .onSuccess { response ->
                        _patchBoardResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getTempBoard() {
            viewModelScope.launch {
                val result = getTempBoardUseCase.getTempBoard()
                result
                    .onSuccess { response ->
                        _getTempBoardResponse.value = response
                    }.onFailure { exception ->
                        when (exception) {
                            is ReissueFailureException -> _navigateToLogin.value = true
                            is NonExistentTempBoardException -> _noTempBoardMessage.value = "임시 저장된 글이 없습니다."
                            else -> _errorMessage.value = exception.message
                        }
                    }
            }
        }
    }
