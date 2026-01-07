package com.catchmate.data.datasource.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.catchmate.domain.NotificationHandler
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class FCMTokenService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationHandler: NotificationHandler
    lateinit var body: String
    lateinit var title: String

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("newToken", "$token")
    }

    override fun handleIntent(intent: Intent?) {
        body = intent?.extras?.getString("gcm.notification.body") ?: ""
        title = intent?.extras?.getString("gcm.notification.title") ?: ""
        Log.i("body title", "$body - $title")
        val new =
            intent?.apply {
                val temp =
                    extras?.apply {
                        remove(MessageNotificationKeys.ENABLE_NOTIFICATION)
                        remove("gcm.notification.e")
                    }
                replaceExtras(temp)
            }
        super.handleIntent(new)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("MESSAGE RECEIVED", "+++++++")

        val data = message.data

        // 채팅 알림 - data = {chatRoomId}
        // 직관 신청 알림 - data = {boardId, acceptStatus}
        Log.i("MSG", "$data / $title / $body")
        if (title.isNotEmpty() && body.isNotEmpty()) {
            showNotification(data, title, body)
        }
    }

    private fun getNotificationBuilder(channelId: String): NotificationCompat.Builder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this@FCMTokenService, channelId)
        } else {
            NotificationCompat.Builder(this@FCMTokenService)
        }

    private fun createNotificationChannel(
        channerId: String,
        channerName: String,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(channerId)

        if (channel == null) {
            val newChannel =
                NotificationChannel(
                    channerId,
                    channerName,
                    NotificationManager.IMPORTANCE_HIGH,
                )
            newChannel.enableVibration(true)
            notificationManager.createNotificationChannel(newChannel)
        }
        val requestCode = UUID.randomUUID().hashCode()
        notificationManager.notify(requestCode, notificationBuilder.build())
    }

    private fun showNotification(
        data: Map<String, String>,
        title: String,
        body: String,
    ) {
//        var channelId: String = ""
//        var channelName: String = ""
//        if (data.containsKey("acceptStatus")) { // 직관 신청 알림
//            channelId = ENROLL_CHANNEL_ID
//            channelName = ENROLL_CHANNEL_NAME
//        } else { // 채팅 알림
//            channelId = CHATTING_CHANNEL_ID
//            channelName = CHATTING_CHANNEL_NAME
//        }
        val notificationBuilder = getNotificationBuilder(CATCHMATE_CHANNEL_ID)
        val builder = notificationHandler.createNotificationBuilder(data, title, body, notificationBuilder)
        createNotificationChannel(CATCHMATE_CHANNEL_ID, CATCHMATE_CHANNEL_NAME, builder)
    }

    suspend fun getToken(): String = FirebaseMessaging.getInstance().token.await()

    companion object {
        const val ENROLL_CHANNEL_ID = "EnrollChannel"
        const val ENROLL_CHANNEL_NAME = "직관 신청 알림"
        const val CHATTING_CHANNEL_ID = "ChattingChannel"
        const val CHATTING_CHANNEL_NAME = "채팅 알림"
        const val EVENT_CHANNEL_ID = "EventChannel"
        const val EVENT_CHANNEL_NAME = "이벤트 알림"
        const val CATCHMATE_CHANNEL_ID = "CatchMateNotificationChannel"
        const val CATCHMATE_CHANNEL_NAME = "캐치메이트 알림"
    }
}
