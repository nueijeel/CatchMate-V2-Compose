package com.catchmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.chatting.DeleteChattingCrewKickOutResponse
import com.catchmate.domain.model.chatting.PatchChattingRoomImageResponse
import com.catchmate.domain.usecase.chatting.KickOutChattingCrewUseCase
import com.catchmate.domain.usecase.chatting.PatchChattingRoomImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ChattingSettingViewModel
    @Inject
    constructor(
        private val kickOutChattingCrewUseCase: KickOutChattingCrewUseCase,
        private val patchChattingRoomImageUseCase: PatchChattingRoomImageUseCase,
    ) : ViewModel() {
        private val _kickOutChattingCrewResponse = MutableLiveData<DeleteChattingCrewKickOutResponse>()
        val kickOutChattingCrewResponse: LiveData<DeleteChattingCrewKickOutResponse>
            get() = _kickOutChattingCrewResponse

        private val _patchChattingRoomImageResponse = MutableLiveData<PatchChattingRoomImageResponse>()
        val patchChattingRoomImageResponse: LiveData<PatchChattingRoomImageResponse>
            get() = _patchChattingRoomImageResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun kickOutChattingCrew(
            chatRoomId: Long,
            userId: Long,
        ) {
            viewModelScope.launch {
                val result = kickOutChattingCrewUseCase(chatRoomId, userId)
                result
                    .onSuccess { response ->
                        _kickOutChattingCrewResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun patchChattingRoomImage(
            chatRoomId: Long,
            chatRoomImage: MultipartBody.Part,
        ) {
            viewModelScope.launch {
                val result = patchChattingRoomImageUseCase(chatRoomId, chatRoomImage)
                result
                    .onSuccess { response ->
                        _patchChattingRoomImageResponse.value = response
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
