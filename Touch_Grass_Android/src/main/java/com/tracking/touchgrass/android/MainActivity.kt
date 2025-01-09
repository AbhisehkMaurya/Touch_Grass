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

/*class TouchGrassViewModel : androidx.lifecycle.ViewModel() {
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted

    private val _touchVerified = MutableStateFlow(false)
    val touchVerified: StateFlow<Boolean> = _touchVerified

    private val _appsBlocked = MutableStateFlow(true)
    val appsBlocked: StateFlow<Boolean> = _appsBlocked

    private val _message = MutableStateFlow("Camera permission is required to use this feature.")
    val message: String
        get() = _message.value

    fun setCameraPermissionGranted(granted: Boolean) {
        _isCameraPermissionGranted.value = granted
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun app(unblock: Boolean) {
        _appsBlocked.value = !unblock
    }

    fun setAppsBlocked(blocked: Boolean) {
        _appsBlocked.value = blocked
    }

    fun onVerifyTouch() {
        viewModelScope.launch {
            // Simulate verification logic (replace with actual verification logic)
            _touchVerified.value = true
        }
    }
}*/


/*@Composable
fun CameraPreview(viewModel: TouchGrassViewModel) {
    val context = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    // Animation state for scaling
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = tween(durationMillis = 400),
        label = "" // Adjust animation duration to 100ms
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter // Align button to the bottom center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build()


                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            (context as ComponentActivity),
                            cameraSelector,
                            preview,
                            imageCapture // Add ImageCapture to camera use case
                        )
                        preview.setSurfaceProvider(this.surfaceProvider)
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Error binding camera use cases", e)
                    }
                }
            }
        )

        // Capture Button with Ripple and Scaling Animation
        Box(
            modifier = Modifier
                .size(80.dp) // Outer Circle Size
                .scale(scale) // Apply scaling animation
                .offset(y = (-30).dp)
                .clip(CircleShape) // Make it circular
                .background(Color.White.copy(alpha = 0.4f)) // Semi-transparent background
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, // Ripple effect source
                    indication = rememberRipple(bounded = false, radius = 40.dp), // Ripple effect
                    onClick = {
                        isPressed = true
                        playCaptureSound(context)
                        imageCapture.takePicture(
                            cameraExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    isPressed = false
                                    processImageWithMLKit(imageProxy, viewModel)
                                    // imageProxy.close()
                                    // Reset scale after capture
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraPreview", "Error capturing photo", exception)
                                    isPressed = false // Reset scale on error
                                }
                            }
                        )
                    }
                ),
            contentAlignment = Alignment.Center // Center Inner Circle
        ) {
            // Inner Circle
            Box(
                modifier = Modifier
                    .size(60.dp) // Inner Circle Size
                    .clip(CircleShape)
                    .background(Color.Green.copy(alpha = 0.6f))
            )
        }
    }
}*/


/**
 * Processes the captured image using ML Kit's `InputImage`.
 */
/*@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun processImageWithMLKit(imageProxy: ImageProxy, viewModel: TouchGrassViewModel) {
    val mediaImage = imageProxy.image
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees

    if (mediaImage != null) {
        CoroutineScope(Dispatchers.IO).launch {
            // Perform ML Kit processing here

            val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            // Pass `inputImage` to your ML Kit model or analysis logic
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(inputImage)
                .addOnSuccessListener { labels ->
                    val detectedLabels = labels.map { it.text }
                    viewModel.updateMessage(detectedLabels.toString())
                    if ("Grass" in detectedLabels && "Hand" in detectedLabels) {
                        viewModel.updateMessage("Touch verified! Apps are unblocked.")
                        viewModel.app(false)

                    } else {
                        viewModel.updateMessage("Keep touching grass!")
                        viewModel.setAppsBlocked(true)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ImageProcessing", "Labeling failed", e)
                }
                .addOnCompleteListener {

                    imageProxy.close()

                }
        }

    } else {
        viewModel.updateMessage("Keep touching grass!")
        imageProxy.close()
    }
}

fun playCaptureSound(context: Context) {
    // Create a MediaPlayer to play the sound
    val mediaPlayer = MediaPlayer.create(context, R.raw.click)

    mediaPlayer.setOnCompletionListener {
        mediaPlayer.release() // Explicitly release the MediaPlayer
    }
    mediaPlayer.start()
}*/


