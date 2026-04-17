package nad.master.pa.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service for push notifications.
 */
@AndroidEntryPoint
class NadFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: ""
        val body  = remoteMessage.notification?.body  ?: ""
        val type  = remoteMessage.data["type"] ?: "general"

        when (type) {
            "session"    -> notificationHelper.showSessionReminder(title, 0)
            "prayer"     -> notificationHelper.showPrayerReminder(title)
            "motivation" -> notificationHelper.showMotivationalMessage(body)
            "sawn"       -> notificationHelper.showSawnReminder(body)
            else         -> notificationHelper.showMotivationalMessage("$title\n$body")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Save token to Firestore under user profile for targeted messaging
    }
}
