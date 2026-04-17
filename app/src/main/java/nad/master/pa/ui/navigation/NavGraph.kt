package nad.master.pa.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nad.master.pa.ui.account.AccountScreen
import nad.master.pa.ui.dashboard.DashboardScreen
import nad.master.pa.ui.dhikr.DhikrScreen
import nad.master.pa.ui.home.HomeScreen
import nad.master.pa.ui.quran.QuranScreen
import nad.master.pa.ui.schedule.ScheduleScreen

@Composable
fun NadNavGraph(
    navController: NavHostController,
    startDestination: String,
    onNavigateToAccount: () -> Unit
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        enterTransition  = {
            slideIntoContainer(
                towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards       = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards       = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards       = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        // No login route — app always starts at Home via anonymous auth
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToAccount = onNavigateToAccount)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Quran.route) {
            QuranScreen()
        }
        composable(Screen.Schedule.route) {
            ScheduleScreen()
        }
        composable(Screen.Dhikr.route) {
            DhikrScreen()
        }
        composable(Screen.Account.route) {
            AccountScreen(onBack = { navController.popBackStack() })
        }
    }
}
