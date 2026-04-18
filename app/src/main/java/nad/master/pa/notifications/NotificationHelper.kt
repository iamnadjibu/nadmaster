package nad.master.pa.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import nad.master.pa.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_SESSIONS  = "nad_sessions"
        const val CHANNEL_PRAYERS   = "nad_prayers"
        const val CHANNEL_MOTIVATION= "nad_motivation"
        const val CHANNEL_PROGRESS  = "nad_progress"

        const val NOTIF_SESSION  = 1001
        const val NOTIF_PRAYER   = 1002
        const val NOTIF_MOTIVATION = 1003
        const val NOTIF_PROGRESS = 1004

        const val ACTION_DONE = "nad.master.pa.ACTION_DONE"
        const val ACTION_MISSED = "nad.master.pa.ACTION_MISSED"
        const val EXTRA_SESSION_ID = "session_id"
    }

    fun createNotificationChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channels = listOf(
            NotificationChannel(
                CHANNEL_SESSIONS,
                "Session Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Reminders for your upcoming sessions" },

            NotificationChannel(
                CHANNEL_PRAYERS,
                "Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Daily prayer time reminders" },

            NotificationChannel(
                CHANNEL_MOTIVATION,
                "Motivation",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Daily motivational messages" },

            NotificationChannel(
                CHANNEL_PROGRESS,
                "Progress Confirmation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Confirm if you completed your sessions" }
        )
        channels.forEach { manager.createNotificationChannel(it) }
    }

    fun showCompletionNotification(sessionTitle: String, sessionId: String) {
        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_DONE
            putExtra(EXTRA_SESSION_ID, sessionId)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context, sessionId.hashCode(), doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val missedIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MISSED
            putExtra(EXTRA_SESSION_ID, sessionId)
        }
        val missedPendingIntent = PendingIntent.getBroadcast(
            context, sessionId.hashCode() + 1, missedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✅ Session Finished: $sessionTitle")
            .setContentText("Did you complete this session?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Done", donePendingIntent)
            .addAction(android.R.drawable.ic_delete, "Missed", missedPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(sessionId.hashCode(), notification)
        } catch (e: SecurityException) { }
    }

    fun showSessionReminder(sessionTitle: String, minutesBefore: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SESSIONS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📅 Upcoming: $sessionTitle")
            .setContentText("Starting in $minutesBefore minutes — get ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_SESSION, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }

    fun showPrayerReminder(prayerName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🕌 Prayer Time: $prayerName")
            .setContentText("حَيَّ عَلَى الصَّلَاةِ — Come to prayer!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_PRAYER, notification)
        } catch (e: SecurityException) { }
    }

    fun showMotivationalMessage(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_MOTIVATION)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✨ NAD MASTER")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_MOTIVATION, notification)
        } catch (e: SecurityException) { }
    }

    fun showSawnReminder(dayDesc: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🌙 Sawn Reminder")
            .setContentText("Today is $dayDesc — a recommended fasting day.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_PRAYER + 1, notification)
        } catch (e: SecurityException) { }
    }
}
