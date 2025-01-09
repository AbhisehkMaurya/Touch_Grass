package com.tracking.touchgrass.android

    import android.accessibilityservice.AccessibilityService
    import android.app.AppOpsManager
    import android.content.ComponentName
    import android.content.Context
    import android.media.MediaPlayer
    import android.provider.Settings
    import android.util.Log
    import androidx.camera.core.ExperimentalGetImage
    import androidx.camera.core.ImageProxy
    import com.google.mlkit.vision.common.InputImage
    import com.google.mlkit.vision.label.ImageLabeling
    import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
    import com.tracking.touchgrass.android.viewmodel.TouchGrassViewModel
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

object PermissionUtils {
        fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<out AccessibilityService>): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val colonSplitter = enabledServices?.split(":")
            val serviceComponentName = ComponentName(context, serviceClass)
            return colonSplitter?.any { it == serviceComponentName.flattenToString() } ?: false
        }


    fun isUsageStatsPermissionGranted(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }



    @androidx.annotation.OptIn(ExperimentalGetImage::class)
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
                            if (("Grass" in detectedLabels || "plant" in detectedLabels) && "Hand" in detectedLabels) {
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
        }



    }

