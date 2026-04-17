package nad.master.pa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import nad.master.pa.ui.navigation.NadNavGraph
import nad.master.pa.ui.navigation.Screen
import nad.master.pa.ui.navigation.bottomNavItems
import nad.master.pa.ui.theme.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NadMasterTheme {
                NadMasterRoot()
            }
        }
    }
}

@Composable
private fun NadMasterRoot() {
    val navController = rememberNavController()
    val auth          = remember { FirebaseAuth.getInstance() }

    // Sign in anonymously and silently — no login screen, ever.
    // This personal app is single-user; the anonymous UID is stable per install.
    var authReady by remember { mutableStateOf(auth.currentUser != null) }
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { authReady = true }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    // Account screen uses its own TopAppBar with Back — hide bottom nav there
    val showBottomNav = currentRoute != Screen.Account.route

    if (!authReady) {
        // Splash / loading state while anonymous sign-in completes
        Box(
            modifier          = Modifier.fillMaxSize(),
            contentAlignment  = Alignment.Center
        ) {
            CircularProgressIndicator(color = WarmCream)
        }
        return
    }

    Scaffold(
        containerColor      = DarkBrown,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar           = {
            if (showBottomNav) {
                NadBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NadNavGraph(
                navController       = navController,
                startDestination    = Screen.Home.route,     // ← Always Home, no login
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
            )
        }
    }
}

@Composable
private fun NadBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MediumBrown,
        tonalElevation = 0.dp,
        modifier       = Modifier.height(72.dp)
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            val scale by animateFloatAsState(
                targetValue   = if (isSelected) 1.15f else 1f,
                animationSpec = tween(200),
                label         = "nav_scale_${item.label}"
            )
            NavigationBarItem(
                selected        = isSelected,
                onClick         = { onNavigate(item.screen.route) },
                icon            = {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier
                            .size(if (item.screen == Screen.Home) 28.dp else 22.dp)
                            .scale(scale)
                    )
                },
                label           = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                },
                alwaysShowLabel = true,
                colors          = NavigationBarItemDefaults.colors(
                    selectedIconColor   = WarmCream,
                    selectedTextColor   = WarmCream,
                    unselectedIconColor = WarmCream.copy(alpha = 0.4f),
                    unselectedTextColor = WarmCream.copy(alpha = 0.4f),
                    indicatorColor      = WarmCream.copy(alpha = 0.12f)
                )
            )
        }
    }
}

@Composable
private fun NadBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MediumBrown,
        tonalElevation = 0.dp,
        modifier       = Modifier.height(72.dp)
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            val scale by animateFloatAsState(
                targetValue  = if (isSelected) 1.15f else 1f,
                animationSpec = tween(200),
                label        = "nav_scale_${item.label}"
            )

            NavigationBarItem(
                selected     = isSelected,
                onClick      = { onNavigate(item.screen.route) },
                icon         = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier    = Modifier.size(if (item.screen == Screen.Home) 28.dp else 22.dp).scale(scale)
                    )
                },
                label        = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = WarmCream,
                    selectedTextColor   = WarmCream,
                    unselectedIconColor = WarmCream.copy(alpha = 0.4f),
                    unselectedTextColor = WarmCream.copy(alpha = 0.4f),
                    indicatorColor      = WarmCream.copy(alpha = 0.12f)
                )
            )
        }
    }
}
