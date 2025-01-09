package com.tracking.touchgrass.android


import FlightPathScreen

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

import com.tracking.touchgrass.android.ui.PermissionRequestScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContent {
            TouchGrassTheme {
                TouchGrassNav()
                // TouchGrassAppStartScreen()

                //   FlightPathScreen()
            }
        }
    }
}

@Composable
fun TouchGrassAppStartScreen() {

    /* val context = LocalContext.current
     val isAccessibilityEnabled = isAccessibilityServiceEnabled(context, AppLockService::class.java)
     val canDrawOverlays = Settings.canDrawOverlays(context)
     val hasUsageStats = isUsageStatsPermissionGranted(context)

     if (!isAccessibilityEnabled || !canDrawOverlays || !hasUsageStats) {
         PermissionScreen()
     } else {
         Text("All permissions granted. Locking apps...")
         // Start AppLockService logic here
     }*/

   /* val viewModel: TouchGrassViewModel = viewModel()
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setCameraPermissionGranted(isGranted)
            viewModel.updateMessage(
                if (isGranted) "Permission granted. You can now use the camera."
                else "Permission denied. Camera access is required."
            )
        }
    )*/

   /* AppScaffold { paddingValues ->
        if (viewModel.isCameraPermissionGranted.collectAsState().value) {
           // CameraPreview(viewModel)
            CameraScreen(
                onVerificationComplete = {},

            )
        } else {
            PermissionRequestScreen(
                onRequestPermission = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                permissionMessage = viewModel.message
            )
        }
    }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(content: @Composable (PaddingValues) -> Unit) {
    Scaffold(

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content(paddingValues)
            }
        }
    )
}


@Composable
fun TouchGrassTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50),
            onPrimary = Color.White,
            secondary = Color(0xFFC8E6C9),
            onSecondary = Color.Black
        ),
        typography = Typography(
            bodyLarge = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            headlineMedium = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        ),
        content = content
    )
}


