package cn.mercury9.omms.connect.desktop.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.mercury9.omms.connect.desktop.data.AppContainer
import cn.mercury9.omms.connect.desktop.ui.screen.LoginScreen

object AppRoutes {
    const val LOGIN_SCREEN = "login_screen"
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
) {
    AppContainer.navController = navController
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_SCREEN
    ) {
        composable(route = AppRoutes.LOGIN_SCREEN) {
            LoginScreen()
        }
    }
}
