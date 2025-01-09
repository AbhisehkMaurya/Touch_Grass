package com.tracking.touchgrass.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.rememberNavController

class LockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController= rememberNavController()
            AppLockScreen { navController.navigate(Routes.Camera) }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LockScreenActivity::class.java)
        }
    }

}
