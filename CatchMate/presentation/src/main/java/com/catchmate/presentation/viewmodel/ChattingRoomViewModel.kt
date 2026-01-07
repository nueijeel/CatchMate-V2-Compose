package com.catchmate.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchmate.domain.exception.ReissueFailureException
import com.catchmate.domain.model.chatting.ChatMessageId
import com.catchmate.domain.model.chatting.ChatMessageInfo
import com.catchmate.domain.model.chatting.ChatRoomInfo
import com.catchmate.domain.model.chatting.DeleteChattingRoomResponse
import com.catchmate.domain.model.chatting.GetChattingCrewListResponse
import com.catchmate.domain.model.chatting.GetChattingHistoryResponse
import com.catchmate.domain.model.chatting.PutChattingRoomAlarmResponse
import com.catchmate.domain.model.enumclass.ChatMessageType
import com.catchmate.domain.usecase.chatting.GetChattingCrewListUseCase
import com.catchmate.domain.usecase.chatting.GetChattingHistoryUseCase
import com.catchmate.domain.usecase.chatting.GetChattingRoomInfoUseCase
import com.catchmate.domain.usecase.chatting.LeaveChattingRoomUseCase
import com.catchmate.domain.usecase.chatting.PutChattingRoomAlarmUseCase
import com.catchmate.presentation.BuildConfig
import com.catchmate.presentation.util.DateUtils.getCurrentTimeFormatted
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
class ChattingRoomViewModel
    @Inject
    constructor(
        private val getChattingHistoryUseCase: GetChattingHistoryUseCase,
        private val getChattingCrewListUseCase: GetChattingCrewListUseCase,
        private val getChattingRoomInfoUseCase: GetChattingRoomInfoUseCase,
        private val deleteChattingRoomUseCase: LeaveChattingRoomUseCase,
        private val putChattingRoomAlarmUseCase: PutChattingRoomAlarmUseCase,
    ) : ViewModel() {
        var topic: Disposable? = null
        var stompClient: StompClient? = null
        private var okHttpClient: OkHttpClient? = null

        private val _getChattingHistoryResponse = MutableLiveData<GetChattingHistoryResponse>()
        val getChattingHistoryResponse: LiveData<GetChattingHistoryResponse>
            get() = _getChattingHistoryResponse

        private val _getChattingCrewListResponse = MutableLiveData<GetChattingCrewListResponse>()
        val getChattingCrewListResponse: LiveData<GetChattingCrewListResponse>
            get() = _getChattingCrewListResponse

        private val _chattingRoomInfo = MutableLiveData<ChatRoomInfo>()
        val chattingRoomInfo: LiveData<ChatRoomInfo>
            get() = _chattingRoomInfo

        private val _deleteChattingRoomResponse = MutableLiveData<DeleteChattingRoomResponse>()
        val deleteChattingRoomResponse: LiveData<DeleteChattingRoomResponse>
            get() = _deleteChattingRoomResponse

        private val _putChattingRoomAlarmResponse = MutableLiveData<PutChattingRoomAlarmResponse>()
        val putChattingRoomAlarmResponse: LiveData<PutChattingRoomAlarmResponse>
            get() = _putChattingRoomAlarmResponse

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?>
            get() = _errorMessage

        private val _navigateToLogin = MutableLiveData<Boolean>()
        val navigateToLogin: LiveData<Boolean>
            get() = _navigateToLogin

        private val _isMessageSent = MutableLiveData<Boolean>()
        val isMessageSent: LiveData<Boolean>
            get() = _isMessageSent

        private val _isInstability = MutableLiveData<Boolean>()
        val isInstability: LiveData<Boolean>
            get() = _isInstability

        /** WebSocket 연결 */
        fun connectToWebSocket(
            chatRoomId: Long,
            userId: Long,
            accessToken: String,
        ) {
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
                        "ChatRoomId" to chatRoomId.toString(),
                    )

                stompClient =
                    Stomp
                        .over(
                            Stomp.ConnectionProvider.OKHTTP,
                            BuildConfig.SERVER_SOCKET_URL,
                            headerMap,
                            okHttpClient,
                        ).apply {
                            withClientHeartbeat(25000)
                            withServerHeartbeat(35000)
                        }

                stompClient?.connect()

                stompClient?.lifecycle()?.subscribe({ event ->
                    when (event.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d("Web Socket✅", "연결 성공")
                            handleWebSocketOpened(chatRoomId, userId)
                        }

                        LifecycleEvent.Type.CLOSED -> {
                            Log.d("Web Socket💤", "연결 해제")
                        }

                        LifecycleEvent.Type.ERROR -> {
                            Log.i("Web Socket", "${event.exception.message}")
                            _isInstability.postValue(true)
                        }

                        else -> {}
                    }
                }, { error ->
                    Log.i("Web Socket", "${error.message}")
                    _isInstability.postValue(true)
                })
            }
        }

        private fun handleWebSocketOpened(
            chatRoomId: Long,
            userId: Long,
        ) {
            // 채팅방 구독
            topic =
                stompClient?.topic("/topic/chat.$chatRoomId")?.subscribe({ message ->
                    Log.i("✅ Msg", message.payload)
                    val jsonObject = JSONObject(message.payload)
                    val messageType = jsonObject.getString("messageType")
                    val chatMessageInfo: ChatMessageInfo =
                        when (messageType) {
                            ChatMessageType.DATE.name -> {
                                val roomId = jsonObject.getString("chatRoomId").toLong()
                                val content = jsonObject.getString("content")
                                val senderId = jsonObject.getString("senderId").toLong()
                                ChatMessageInfo(
                                    chatMessageId = "",
                                    roomId = roomId,
                                    content = content,
                                    senderId = senderId,
                                    messageType = messageType,
                                )
                            }

                            ChatMessageType.TALK.name -> {
                                val chatMessageId = jsonObject.getString("chatMessageId")
                                val senderId = jsonObject.getString("senderId").toLong()
                                val content = jsonObject.getString("content")
                                val roomId = jsonObject.getString("roomId").toLong()
                                val id = ChatMessageId(date = getCurrentTimeFormatted())
                                ChatMessageInfo(
                                    id = id,
                                    chatMessageId = chatMessageId,
                                    roomId = roomId,
                                    content = content,
                                    senderId = senderId,
                                    messageType = messageType,
                                )
                            }

                            else -> { // 채팅방 나가고 들어올때 메시지 처리하기
                                ChatMessageInfo(chatMessageId = "", roomId = -1L, content = "", senderId = -1L, messageType = "")
                            }
                        }
                    addChatMessage(chatMessageInfo)
                    sendIsMsgRead(chatRoomId, userId)
                }, { error ->
                    Log.i("ws opened", "chatroom subscribe error / ${error.printStackTrace()}", error)
                })
        }

        private fun sendIsMsgRead(
            chatRoomId: Long,
            userId: Long,
        ) {
            viewModelScope.launch {
                val msg =
                    JSONObject()
                        .apply {
                            put("chatRoomId", chatRoomId)
                            put("userId", userId)
                        }.toString()
                stompClient?.send("/app/chat/read", msg)?.subscribe()
            }
        }

        fun sendMessage(
            chatRoomId: Long,
            message: String,
        ) {
            // 전달 성공 시 view의 edt 텍스트 비우기
            viewModelScope.launch {
                stompClient?.send("/app/chat.$chatRoomId", message)?.subscribe({
                    Log.d("Web Socket📬", "메시지 전달")
                    _isMessageSent.value = true
                }, { error ->
                    Log.d("Web Socket✉️❌", "메시지 전송 실패", error)
                    _isMessageSent.value = false
                })
            }
        }

        override fun onCleared() {
            super.onCleared()
            topic?.dispose()
            stompClient?.disconnect()
        }

        private fun addChatMessage(chatMessageInfo: ChatMessageInfo) {
            val currentList = _getChattingHistoryResponse.value?.chatMessageInfoList ?: emptyList()
            val updatedList = listOf(chatMessageInfo) + currentList

            val updatedResponse =
                _getChattingHistoryResponse.value?.copy(
                    chatMessageInfoList = updatedList,
                ) ?: GetChattingHistoryResponse(
                    chatMessageInfoList = updatedList,
                    isFirst = true,
                    isLast = true,
                    lastMessageId = updatedList.last().chatMessageId,
                )

            _getChattingHistoryResponse.postValue(updatedResponse)
        }

        fun getChattingHistory(
            chatRoomId: Long,
            lastMessageId: String? = null,
            size: Int? = 20,
        ) {
            viewModelScope.launch {
                val result = getChattingHistoryUseCase(chatRoomId, lastMessageId, size)
                result
                    .onSuccess { response ->
                        _getChattingHistoryResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getChattingCrewList(chatRoomId: Long) {
            viewModelScope.launch {
                val result = getChattingCrewListUseCase(chatRoomId)
                result
                    .onSuccess { response ->
                        _getChattingCrewListResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun getChattingRoomInfo(chatRoomId: Long) {
            viewModelScope.launch {
                val result = getChattingRoomInfoUseCase(chatRoomId)
                result
                    .onSuccess { info ->
                        _chattingRoomInfo.value = info
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun deleteChattingRoom(chatRoomId: Long) {
            viewModelScope.launch {
                val result = deleteChattingRoomUseCase(chatRoomId)
                result
                    .onSuccess { response ->
                        _deleteChattingRoomResponse.value = response
                    }.onFailure { exception ->
                        if (exception is ReissueFailureException) {
                            _navigateToLogin.value = true
                        } else {
                            _errorMessage.value = exception.message
                        }
                    }
            }
        }

        fun putChattingRoomAlarm(
            chatRoomId: Long,
            enable: Boolean,
        ) {
            viewModelScope.launch {
                val result = putChattingRoomAlarmUseCase(chatRoomId, enable)
                result
                    .onSuccess { response ->
                        _putChattingRoomAlarmResponse.value = response
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
