import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.tracking.touchgrass.android.AppLockService
import com.tracking.touchgrass.android.PermissionUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionScreen { allPermissionsGranted ->
                if (allPermissionsGranted) {
                    // Navigate to the next screen or proceed
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(onAllPermissionsGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    var isAccessibilityEnabled by remember { mutableStateOf(PermissionUtils.isAccessibilityServiceEnabled(context, AppLockService::class.java)) }
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var hasUsageStatsPermission by remember { mutableStateOf(PermissionUtils.isUsageStatsPermissionGranted(context)) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    val allPermissionsGranted = isAccessibilityEnabled && canDrawOverlays && hasUsageStatsPermission && hasCameraPermission

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Manage Permissions", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        SwitchWithLabel(
            label = "Enable Accessibility Service",
            isChecked = isAccessibilityEnabled,
            onToggle = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            }
        )

        SwitchWithLabel(
            label = "Enable Overlay Permission",
            isChecked = canDrawOverlays,
            onToggle = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        )

        SwitchWithLabel(
            label = "Enable Usage Stats Permission",
            isChecked = hasUsageStatsPermission,
            onToggle = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        )

        SwitchWithLabel(
            label = "Enable Camera Permission",
            isChecked = hasCameraPermission,
            onToggle = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onAllPermissionsGranted(allPermissionsGranted) },
            enabled = allPermissionsGranted
        ) {
            Text(text = if (allPermissionsGranted) "Continue" else "Grant All Permissions")
        }
    }
}

@Composable
fun SwitchWithLabel(label: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            enabled = !isChecked // Disable the switch if permission is already granted
        )
    }
}
