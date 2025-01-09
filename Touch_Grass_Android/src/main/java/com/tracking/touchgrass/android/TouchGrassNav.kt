package com.tracking.touchgrass.android

import PermissionScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun TouchGrassNav() {
    val navController = rememberNavController() // Create a NavController

    // Define NavHost with all routes
    NavHost(
        navController = navController,
        startDestination = Routes.Landing
    ) {
        composable(Routes.Landing) {
            LandingScreen(
                onGetStarted = { navController.navigate(Routes.Permissions) })
        }

        composable(Routes.Permissions) {

            PermissionScreen { allPermissionsGranted ->
                if (allPermissionsGranted) {
                    navController.navigate(Routes.AppLock)
                }
            }

        }
        composable(Routes.AppLock) {
            AppLockScreen { navController.navigate(Routes.Camera) }
        }

        composable(Routes.Camera) {
            CameraScreen { isVerified ->
                if (isVerified) navController.navigate(Routes.Unlock)
            }
        }
        composable(Routes.Unlock) {
            UnlockAppScreen { navController.navigate(Routes.Landing) }
        }
    }

}
