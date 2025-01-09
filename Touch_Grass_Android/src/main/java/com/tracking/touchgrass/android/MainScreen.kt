import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracking.touchgrass.android.R

class MainScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlightPathScreen()
        }
    }
}

@Composable
fun FlightPathScreen() {
    // List of cities and their respective positions on the static map
    val cities = mapOf(
        "New Delhi" to Offset(120f, 400f),
        "Bengaluru" to Offset(1100f, 300f),
        "Mumbai" to Offset(900f, 800f),
        "Kolkata" to Offset(800f, 300f)
    )

    var selectedFromCity by remember { mutableStateOf<String?>(null) }
    var selectedToCity by remember { mutableStateOf<String?>(null) }

    // Animation state for flight movement
    val infiniteTransition = rememberInfiniteTransition()

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
        )
    )

    Box(modifier = Modifier.wrapContentSize().fillMaxWidth().fillMaxHeight().background(Color.White)) {
        // Static map background
        Image(
            painter = painterResource(id = R.drawable.fi), // Replace with your map image
            contentDescription = "Map",

            contentScale = ContentScale.Fit,
            modifier = Modifier.wrapContentSize()
        )

        // Draw cities and flight path
        Canvas(modifier = Modifier.wrapContentSize()) {
            // Draw city dots
            cities.forEach { (_, position) ->
                drawCircle(
                    color = Color.Green,
                    radius = 15f,
                    center = position
                )
            }

            // Draw flight path if both cities are selected
            if (selectedFromCity != null && selectedToCity != null) {
                val fromPosition = cities[selectedFromCity]!!
                val toPosition = cities[selectedToCity]!!

                val controlPoint = Offset((fromPosition.x + toPosition.x) / 2, fromPosition.y - 150f)


                drawCurvedLine(fromPosition, toPosition,controlPoint)
            }
        }

        // Overlay the flight icon dynamically
        if (selectedFromCity != null && selectedToCity != null) {
            val fromPosition = cities[selectedFromCity]!!
            val toPosition = cities[selectedToCity]!!
            val controlPoint = Offset((fromPosition.x + toPosition.x) / 2, fromPosition.y - 150f)

            val animatedPosition = calculateAnimatedPosition(fromPosition, toPosition, controlPoint,animatedProgress)

            val animatedPositionInDp = with(LocalDensity.current) {
                Offset(
                    x = animatedPosition.x.toDp().value - 16, // Offset to center the icon
                    y = animatedPosition.y.toDp().value - 16 // Offset to center the icon
                )
            }

            val rotationAngle = calculateRotationAngle(fromPosition, toPosition, controlPoint, animatedProgress)


            FlightIcon(position = animatedPositionInDp, rotation = rotationAngle)
           // FlightIcon(position = animatedPositionInDp)


        }

      // City selection UI
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "From", fontSize = 14.sp, color = Color.Black)
                    CityDropdown(
                        cities.keys.toList(),
                        selectedCity = selectedFromCity,
                        onCitySelected = { selectedFromCity = it }
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "To", fontSize = 14.sp, color = Color.Black)
                    CityDropdown(
                        cities.keys.toList(),
                        selectedCity = selectedToCity,
                        onCitySelected = { selectedToCity = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Trigger animation or additional logic
                }
            ) {
                Text(text = "Search Flight")
            }
        }
    }
}


@Composable
fun FlightIcon(position: Offset, rotation: Float) {
    Image(
        painter = painterResource(id = R.drawable.f), // Replace with your flight icon drawable
        contentDescription = "Flight Icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(x = position.x.dp, y = position.y.dp) // Position dynamically
            // Set size of the icon
            .size(32.dp)
            .graphicsLayer { rotationZ = rotation } // Apply rotation
    )
}


// Dropdown for selecting cities
@Composable
fun CityDropdown(
    cities: List<String>,
    selectedCity: String?,
    onCitySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)) {
        Button(onClick = { expanded = !expanded }) {
            Text(
                text = selectedCity ?: "Select City",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(text = city, fontSize = 14.sp) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}


// Function to draw a curved line between two positions
fun DrawScope.drawCurvedLine(from: Offset, to: Offset, control: Offset) {
    val path = Path().apply {
        moveTo(from.x, from.y) // Start point
        quadraticBezierTo(control.x, control.y, to.x, to.y) // Draw curve
    }

    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 5f)
    )
}

// Function to calculate the animated position along the path
fun calculateAnimatedPosition(from: Offset, to: Offset, control: Offset, progress: Float): Offset {
    val t = progress
    val x = (1 - t) * (1 - t) * from.x + 2 * (1 - t) * t * control.x + t * t * to.x
    val y = (1 - t) * (1 - t) * from.y + 2 * (1 - t) * t * control.y + t * t * to.y

    return Offset(x, y)
}

// Updated function to calculate rotation angle
fun calculateRotationAngle(from: Offset, to: Offset, control: Offset, progress: Float): Float {
    // Calculate the tangent vector at point t on the curve
    val dx = 2 * (1 - progress) * (control.x - from.x) + 2 * progress * (to.x - control.x)
    val dy = 2 * (1 - progress) * (control.y - from.y) + 2 * progress * (to.y - control.y)

    // Calculate the angle of the tangent vector
    return Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
}
