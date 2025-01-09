package com.tracking.touchgrass.android

import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracking.touchgrass.android.PermissionUtils.playCaptureSound
import com.tracking.touchgrass.android.PermissionUtils.processImageWithMLKit
import com.tracking.touchgrass.android.ui.PermissionRequestScreen
import com.tracking.touchgrass.android.viewmodel.TouchGrassViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(onVerificationComplete: (Boolean) -> Unit) {

    val viewModel: TouchGrassViewModel = viewModel()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setCameraPermissionGranted(isGranted)
            viewModel.updateMessage(
                if (isGranted) "Permission granted. You can now use the camera."
                else "Permission denied. Camera access is required."
            )
        }
    )
    AppScaffold { paddingValues ->
        if (viewModel.isCameraPermissionGranted.collectAsState().value) {
            /*CameraScreen(
                onVerificationComplete = {},
            )*/
        } else {
            PermissionRequestScreen(
                onRequestPermission = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                permissionMessage = viewModel.message
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Align your hand and grass.")
        Button(onClick = { onVerificationComplete(true) }) {
            Text("Verify")
        }
    }


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
                    .offset(y=(-30).dp)
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
    }


