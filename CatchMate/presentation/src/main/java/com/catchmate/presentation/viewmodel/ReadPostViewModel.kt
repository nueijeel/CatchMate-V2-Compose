package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.BlockedUserBoardException
import com.catchmate.domain.exception.BookmarkFailureException
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.board.DeleteBoardLikeResponse
import com.catchmate.domain.model.board.DeleteBoardResponse
import com.catchmate.domain.model.board.GetBoardResponse
import com.catchmate.domain.model.board.PatchBoardLiftUpResponse
import com.catchmate.domain.model.board.PostBoardLikeResponse
import com.catchmate.domain.model.enroll.DeleteEnrollResponse
import com.catchmate.domain.model.enroll.GetRequestedEnrollResponse
import com.catchmate.domain.model.enroll.PostEnrollRequest
import com.catchmate.domain.model.enroll.PostEnrollResponse
import com.catchmate.domain.model.enumclass.EnrollState
import com.catchmate.domain.usecase.board.DeleteBoardLikeUseCase
import com.catchmate.domain.usecase.board.DeleteBoardUseCase
import com.catchmate.domain.usecase.board.GetBoardUseCase
import com.catchmate.domain.usecase.board.PatchBoardLiftUpUseCase
import com.catchmate.domain.usecase.board.PostBoardLikeUseCase
import com.catchmate.domain.usecase.enroll.DeleteEnrollUseCase
import com.catchmate.domain.usecase.enroll.GetRequestedEnrollUseCase
import com.catchmate.domain.usecase.enroll.PostEnrollUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadPostViewModel
    @Inject
    constructor(
        private val getBoardUseCase: GetBoardUseCase,
        private val deleteBoardUseCase: DeleteBoardUseCase,
        private val postBoardLikeUseCase: PostBoardLikeUseCase,
        private val deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
        private val postEnrollUseCase: PostEnrollUseCase,
        private val patchBoardLiftUpUseCase: PatchBoardLiftUpUseCase,
        private val getRequestedEnrollUseCase: GetRequestedEnrollUseCase,
        private val deleteEnrollUseCase: DeleteEnrollUseCase,
    ) : ViewModel() {
        private val _getBoardResponse = MutableLiveData<GetBoardResponse>()
        val getBoardResponse: LiveData<GetBoardResponse>
            get() = _getBoardResponse

        private val _postBoardLikeResponse = MutableLiveData<PostBoardLikeResponse>()
        val postBoardLikeResponse: LiveData<PostBoardLikeResponse>
            get() = _postBoardLikeResponse

        private val _deleteBoardLikeResponse = MutableLiveData<DeleteBoardLikeResponse>()
        val deleteBoardLikeResponse: LiveData<DeleteBoardLikeResponse>
            get() = _deleteBoardLikeResponse

        private val _boardEnrollState = MutableLiveData<EnrollState>()
        val boardEnrollState: LiveData<EnrollState>
            get() = _boardEnrollState

        private val _postEnrollResponse = MutableLiveData<PostEnrollResponse>()
        val postEnrollResponse: LiveData<PostEnrollResponse>
            get() = _postEnrollResponse

        private val _deleteBoardResponse = MutableLiveData<DeleteBoardResponse>()
        val deleteBoardResponse: LiveData<DeleteBoardResponse>
            get() = _deleteBoardResponse

        private val _patchBoardLiftUpResponse = MutableLiveData<PatchBoardLiftUpResponse>()
        val patchBoardLiftUpResponse: LiveData<PatchBoardLiftUpResponse>
            get() = _patchBoardLiftUpResponse

        private val _getRequestedEnroll = MutableLiveData<GetRequestedEnrollResponse>()
        val getRequestedEnroll: LiveData<GetRequestedEnrollResponse>
            get() = _getRequestedEnroll

        private val _deleteEnrollResponse = MutableLiveData<DeleteEnrollResponse>()
        val deleteEnrollResponse: LiveData<DeleteEnrollResponse>
            get() = _deleteEnrollResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        private val _bookmarkFailureMessage = MutableLiveData<String>()
        val bookmarkFailureMessage: LiveData<String>
            get() = _bookmarkFailureMessage

        private val _blockedUserBoardMessage = MutableLiveData<String>()
        val blockedUserBoardMessage: LiveData<String>
            get() = _blockedUserBoardMessage

        fun getBoard(boardId: Long) {
            viewModelScope.launch {
                val result = getBoardUseCase.getBoard(boardId)
                result
                    .onSuccess { response ->
                        _getBoardResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else if (exception is BlockedUserBoardException) {
                            _blockedUserBoardMessage.value = exception.message
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun postBoardLike(boardId: Long) {
            viewModelScope.launch {
                val result = postBoardLikeUseCase.postBoardLike(boardId)
                result
                    .onSuccess { response ->
                        _postBoardLikeResponse.value = response
                    }.onFailure { exception ->
                        when (exception) {
                            is ReissueFailureException -> _navigateToLogin.value = true
                            is BookmarkFailureException -> _bookmarkFailureMessage.value = exception.message
                            else -> _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun deleteBoardLike(boardId: Long) {
            viewModelScope.launch {
                val result = deleteBoardLikeUseCase.deleteBoardLike(boardId)
                result
                    .onSuccess { response ->
                        _deleteBoardLikeResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun postEnroll(
            boardId: Long,
            postEnrollRequest: PostEnrollRequest,
        ) {
            viewModelScope.launch {
                val result = postEnrollUseCase.postEnroll(boardId, postEnrollRequest)
                result
                    .onSuccess { response ->
                        _postEnrollResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun setBoardEnrollState(state: EnrollState) {
            _boardEnrollState.value = state
        }

        fun deleteBoard(boardId: Long) {
            viewModelScope.launch {
                val result = deleteBoardUseCase.deleteBoard(boardId)
                result
                    .onSuccess { response ->
                        _deleteBoardResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun patchBoardLiftUp(boardId: Long) {
            viewModelScope.launch {
                val result = patchBoardLiftUpUseCase.patchBoardLiftUp(boardId)
                result
                    .onSuccess { response ->
                        _patchBoardLiftUpResponse.value = response
                    }.onFailure { exception ->
                        when (exception) {
                            is ReissueFailureException -> {
                                _navigateToLogin.value = true
                            }

                            else -> {
                                _errorMessage.value = exception.message
                            }
                        }
                    }
            }
        }

        fun getRequestedEnroll(boardId: Long) {
            viewModelScope.launch {
                val result = getRequestedEnrollUseCase(boardId)
                result
                    .onSuccess { response ->
                        _getRequestedEnroll.value = response
                    }.onFailure { exception ->
                        when (exception) {
                            is ReissueFailureException -> {
                                _navigateToLogin.value = true
                            }

                            else -> {
                                _errorMessage.value = exception.message
                            }
                        }
                    }
            }
        }

        fun deleteEnroll(enrollId: Long) {
            viewModelScope.launch {
                val result = deleteEnrollUseCase.deleteEnroll(enrollId)
                result
                    .onSuccess { response ->
                        _deleteEnrollResponse.value = response
                    }.onFailure { exception ->
                        when (exception) {
                            is ReissueFailureException -> {
                                _navigateToLogin.value = true
                            }

                            else -> {
                                _errorMessage.value = exception.message
                            }
                        }
                    }
            }
        }
    }
