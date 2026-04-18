package nad.master.pa.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Worker that fires when a session is supposed to end, 
 * asking the user if they completed it.
 */
@HiltWorker
class SessionCompletionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val sessionTitle = inputData.getString("session_title") ?: "Your session"
        val sessionId = inputData.getString("session_id") ?: return Result.failure()
        
        notificationHelper.showCompletionNotification(sessionTitle, sessionId)
        return Result.success()
    }

    companion object {
        const val WORK_TAG = "session_completion_"

        fun scheduleCompletionCheck(
            context: Context,
            sessionId: String,
            sessionTitle: String,
            delayMinutes: Long
        ) {
            val inputData = workDataOf(
                "session_id" to sessionId,
                "session_title" to sessionTitle
            )

            // Overwrite existing check for this session if any
            val request = OneTimeWorkRequestBuilder<SessionCompletionWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag(WORK_TAG + sessionId)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_TAG + sessionId,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
        
        fun cancelCheck(context: Context, sessionId: String) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG + sessionId)
        }
    }
}
