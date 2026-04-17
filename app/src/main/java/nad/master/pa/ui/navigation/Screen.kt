package nad.master.pa.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Bottom Nav Screens
    object Dashboard   : Screen("dashboard")
    object Schedule    : Screen("schedule")
    object Home        : Screen("home")
    object Quran       : Screen("quran")
    object Dhikr       : Screen("dhikr")
    // Top-right Account
    object Account     : Screen("account")
    // Auth
    object Login       : Screen("login")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", Icons.Filled.Dashboard),
    BottomNavItem(Screen.Schedule,  "Schedule",  Icons.Filled.CalendarMonth),
    BottomNavItem(Screen.Home,      "Home",      Icons.Filled.Home),
    BottomNavItem(Screen.Quran,     "Quran",     Icons.Filled.AutoStories),
    BottomNavItem(Screen.Dhikr,     "Dhikr",     Icons.Filled.SelfImprovement),
)
