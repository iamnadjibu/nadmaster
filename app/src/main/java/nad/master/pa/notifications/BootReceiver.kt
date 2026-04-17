package nad.master.pa.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receives BOOT_COMPLETED to reschedule WorkManager reminder chains
 * after a device restart.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // WorkManager tasks persist across reboots automatically, but if
            // any were lost we can re-enqueue them here.
            // SessionReminderWorker.scheduleDaily(context)
        }
    }
}
