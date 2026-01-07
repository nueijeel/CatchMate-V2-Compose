package com.catchmate.presentation.viewmodel

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.chatting.DeleteChattingRoomResponse
import com.catchmate.domain.model.chatting.GetChattingRoomListResponse
import com.catchmate.domain.usecase.chatting.GetChattingRoomListUseCase
import com.catchmate.domain.usecase.chatting.LeaveChattingRoomUseCase
import com.catchmate.presentation.BuildConfig
import com.catchmate.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject

@HiltViewModel
class ChattingHomeViewModel
    @Inject
    constructor(
        private val getChattingRoomListUseCase: GetChattingRoomListUseCase,
        private val leaveChattingRoomUseCase: LeaveChattingRoomUseCase,
    ) : ViewModel() {
        var topic: Disposable? = null
        var stompClient: StompClient? = null
        private var okHttpClient: OkHttpClient? = null

        private val _getChattingRoomListResponse = MutableLiveData<GetChattingRoomListResponse>()
        val getChattingRoomListResponse: LiveData<GetChattingRoomListResponse>
            get() = _getChattingRoomListResponse

        private val _leaveChattingRoomResponse = MutableLiveData<DeleteChattingRoomResponse>()
        val leaveChattingRoomResponse: LiveData<DeleteChattingRoomResponse>
            get() = _leaveChattingRoomResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        fun connectToWebSocket(accessToken: String) {
            viewModelScope.launch {
                okHttpClient =
                    OkHttpClient
                        .Builder()
                        .addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            },
                        ).build()

                val headerMap =
                    mapOf(
                        "AccessToken" to accessToken,
                    )

                stompClient =
                    Stomp.over(
                        Stomp.ConnectionProvider.OKHTTP,
                        BuildConfig.SERVER_SOCKET_URL,
                        headerMap,
                        okHttpClient,
                    )

                stompClient?.connect()

                stompClient?.lifecycle()?.subscribe { event ->
                    when (event.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d("Web Socket✅", "연결 성공")
                            handleWebSocketOpened()
                        }

                        LifecycleEvent.Type.CLOSED -> {
                            Log.d("Web Socket💤", "연결 해제")
                        }

                        LifecycleEvent.Type.ERROR -> {
                            Log.i("Web Socket", "${event.exception}")
                        }

                        else -> {}
                    }
                }
            }
        }

        private fun handleWebSocketOpened() {
            topic =
                stompClient?.topic("/topic/chatList")!!.subscribe { msg ->
                    Log.i("✅ New Msg", msg.payload)
                    val jsonObject = JSONObject(msg.payload)
                    val chatRoomId = jsonObject.getString("chatRoomId").toLong()
                    val content = jsonObject.getString("content")
                    val sentTime = jsonObject.getString("sendTime")
                    updateLastChat(chatRoomId, content, sentTime)
                }
        }

        private fun updateLastChat(
            chatRoomId: Long,
            newContent: String,
            newSentTime: String,
        ) {
            val currentResponse = _getChattingRoomListResponse.value
            val currentList = (currentResponse?.chatRoomInfoList ?: emptyList()).toMutableList()
            val targetIndex = currentList.indexOfFirst { it.chatRoomId == chatRoomId }
            if (currentList.isNotEmpty() && targetIndex != -1) { // 해당하는 채팅방이 현재 목록에 있을때만 반영되도록
                currentList[targetIndex] =
                    currentList[targetIndex].copy(
                        lastMessageContent = newContent,
                        lastMessageAt = newSentTime,
                        isNewChatRoom = false,
                        unreadMessageCount = currentList[targetIndex].unreadMessageCount + 1,
                    )
                val updatedResponse = currentResponse?.copy(chatRoomInfoList = currentList)!!
                _getChattingRoomListResponse.postValue(updatedResponse)
            }
        }

        override fun onCleared() {
            super.onCleared()
            topic?.dispose()
            stompClient?.disconnect()
        }

        fun getChattingRoomList(page: Int) {
            viewModelScope.launch {
                val result = getChattingRoomListUseCase.getChattingRoomList(page)
                result
                    .onSuccess { response ->
                        _getChattingRoomListResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = "ListLoadError"
                        }
                    }
            }
        }

        fun leaveChattingRoom(chatRoomId: Long) {
            viewModelScope.launch {
                val result = leaveChattingRoomUseCase(chatRoomId)
                result
                    .onSuccess { response ->
                        _leaveChattingRoomResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            Log.d("ChattingHomeVM", exception.message.toString())
                            _errorMessage.value = "LeaveChattingRoomError"
                        }
                    }
            }
        }
    }
