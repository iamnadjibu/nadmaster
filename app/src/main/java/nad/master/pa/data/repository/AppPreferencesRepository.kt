package nad.master.pa.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("NadMasterLocalData", Context.MODE_PRIVATE)

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String {
        return prefs.getString("user_name", "") ?: ""
    }

    fun setLastAiSync(timestamp: Long) {
        prefs.edit().putLong("last_ai_sync", timestamp).apply()
    }

    fun getLastAiSync(): Long {
        return prefs.getLong("last_ai_sync", 0L)
    }

    fun setSpiritualGoal(goal: String) {
        prefs.edit().putString("spiritual_goal", goal).apply()
    }

    fun getSpiritualGoal(): String {
        return prefs.getString("spiritual_goal", "") ?: ""
    }
    
    fun setTargetJuzz(juzz: Int) {
        prefs.edit().putInt("target_juzz_per_month", juzz).apply()
    }
    
    fun getTargetJuzz(): Int {
        return prefs.getInt("target_juzz_per_month", 1)
    }
}
