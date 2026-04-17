package nad.master.pa.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that fires daily session reminders.
 * Scheduled once and repeats daily.
 */
@HiltWorker
class SessionReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val sessionTitle = inputData.getString("session_title") ?: "Your session"
        val minutesBefore = inputData.getInt("minutes_before", 15)
        notificationHelper.showSessionReminder(sessionTitle, minutesBefore)
        return Result.success()
    }

    companion object {
        const val WORK_TAG = "session_reminder"

        fun scheduleReminder(
            context: Context,
            sessionTitle: String,
            delayMinutes: Long,
            minutesBefore: Int = 15
        ) {
            val inputData = workDataOf(
                "session_title"  to sessionTitle,
                "minutes_before" to minutesBefore
            )

            val request = OneTimeWorkRequestBuilder<SessionReminderWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
