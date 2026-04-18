package nad.master.pa.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.repository.SessionRepository
import javax.inject.Inject

/**
 * Handles actions from the Session Completion notification.
 */
@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionRepository: nad.master.pa.data.repository.SessionRepository

    @Inject
    lateinit var performanceRepository: nad.master.pa.data.repository.PerformanceRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getStringExtra(NotificationHelper.EXTRA_SESSION_ID) ?: return
        val action = intent.action ?: return

        Log.d("NotifReceiver", "Received action: $action for session: $sessionId")

        // Dismiss the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(sessionId.hashCode())

        scope.launch {
            try {
                when (action) {
                    NotificationHelper.ACTION_DONE -> {
                        sessionRepository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)
                        performanceRepository.refreshTodayPerformance()
                    }
                    NotificationHelper.ACTION_MISSED -> {
                        sessionRepository.updateSessionStatus(sessionId, SessionStatus.MISSED)
                        performanceRepository.refreshTodayPerformance()
                    }
                }
            } catch (e: Exception) {
                Log.e("NotifReceiver", "Error updating session and refreshing", e)
            }
        }
    }
}
