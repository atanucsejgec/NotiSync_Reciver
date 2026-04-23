// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/ui/navigation/ReceiverNavGraph.kt
// Purpose: Defines all navigation routes and screen transitions
// ============================================================

package com.app.notisync_receiver.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.notisync_receiver.data.repository.AuthState
import com.app.notisync_receiver.ui.screens.CallLogScreen
import com.app.notisync_receiver.ui.screens.DeviceListScreen
import com.app.notisync_receiver.ui.screens.KeyboardScreen
import com.app.notisync_receiver.ui.screens.LocationScreen
import com.app.notisync_receiver.ui.screens.LoginScreen
import com.app.notisync_receiver.ui.screens.NotificationListScreen
import com.app.notisync_receiver.ui.screens.SettingsScreen
import com.app.notisync_receiver.viewmodel.AuthViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ReceiverRoutes {
    const val LOGIN = "login"
    const val DEVICE_LIST = "device_list"
    const val NOTIFICATION_LIST = "notification_list/{deviceId}/{deviceName}"
    const val LOCATION = "location/{deviceId}/{deviceName}"
    const val CALL_LOGS = "call_logs/{deviceId}/{deviceName}"
    const val KEYBOARD = "keyboard/{deviceId}/{deviceName}"
    const val SETTINGS = "settings"

    fun notificationList(deviceId: String, deviceName: String): String {
        return "notification_list/$deviceId/${encode(deviceName)}"
    }

    fun location(deviceId: String, deviceName: String): String {
        return "location/$deviceId/${encode(deviceName)}"
    }

    fun callLogs(deviceId: String, deviceName: String): String {
        return "call_logs/$deviceId/${encode(deviceName)}"
    }

    fun keyboard(deviceId: String, deviceName: String): String {
        return "keyboard/$deviceId/${encode(deviceName)}"
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }
}

@Composable
fun ReceiverNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Extract deviceId and deviceName from arguments if they exist
    val deviceId = navBackStackEntry?.arguments?.getString("deviceId")
    val deviceName = navBackStackEntry?.arguments?.getString("deviceName")

    val showBottomBar = currentDestination?.route in listOf(
        ReceiverRoutes.NOTIFICATION_LIST,
        ReceiverRoutes.LOCATION,
        ReceiverRoutes.CALL_LOGS,
        ReceiverRoutes.KEYBOARD
    )

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                if (currentDestination?.route == ReceiverRoutes.LOGIN) {
                    navController.navigate(ReceiverRoutes.DEVICE_LIST) {
                        popUpTo(ReceiverRoutes.LOGIN) { inclusive = true }
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(ReceiverRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    val startDestination = when (authState) {
        is AuthState.Authenticated -> ReceiverRoutes.DEVICE_LIST
        else -> ReceiverRoutes.LOGIN
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar && deviceId != null && deviceName != null) {
                NavigationBar {
                    val items = listOf(
                        Triple(ReceiverRoutes.NOTIFICATION_LIST, Icons.Default.Notifications, "Notifications"),
                        Triple(ReceiverRoutes.LOCATION, Icons.Default.LocationOn, "Location"),
                        Triple(ReceiverRoutes.CALL_LOGS, Icons.Default.Call, "Calls"),
                        Triple(ReceiverRoutes.KEYBOARD, Icons.Default.Keyboard, "Keyboard")
                    )

                    items.forEach { (routeTemplate, icon, label) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == routeTemplate } == true,
                            onClick = {
                                val route = when (routeTemplate) {
                                    ReceiverRoutes.NOTIFICATION_LIST -> ReceiverRoutes.notificationList(deviceId, deviceName)
                                    ReceiverRoutes.LOCATION -> ReceiverRoutes.location(deviceId, deviceName)
                                    ReceiverRoutes.CALL_LOGS -> ReceiverRoutes.callLogs(deviceId, deviceName)
                                    ReceiverRoutes.KEYBOARD -> ReceiverRoutes.keyboard(deviceId, deviceName)
                                    else -> routeTemplate
                                }

                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(ReceiverRoutes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel
                )
            }

            composable(ReceiverRoutes.DEVICE_LIST) {
                DeviceListScreen(
                    onNavigateToNotifications = { deviceId, deviceName ->
                        navController.navigate(ReceiverRoutes.notificationList(deviceId, deviceName))
                    },
                    onNavigateToSettings = {
                        navController.navigate(ReceiverRoutes.SETTINGS)
                    }
                )
            }

            composable(
                route = ReceiverRoutes.NOTIFICATION_LIST,
                arguments = listOf(
                    navArgument("deviceId") { type = NavType.StringType },
                    navArgument("deviceName") { type = NavType.StringType }
                )
            ) {
                NotificationListScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ReceiverRoutes.LOCATION,
                arguments = listOf(
                    navArgument("deviceId") { type = NavType.StringType },
                    navArgument("deviceName") { type = NavType.StringType }
                )
            ) {
                LocationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ReceiverRoutes.CALL_LOGS,
                arguments = listOf(
                    navArgument("deviceId") { type = NavType.StringType },
                    navArgument("deviceName") { type = NavType.StringType }
                )
            ) {
                CallLogScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ReceiverRoutes.KEYBOARD,
                arguments = listOf(
                    navArgument("deviceId") { type = NavType.StringType },
                    navArgument("deviceName") { type = NavType.StringType }
                )
            ) {
                KeyboardScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(ReceiverRoutes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
