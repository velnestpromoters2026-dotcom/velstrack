package com.velstrack.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.ElectricIndigo
import com.velstrack.app.core.theme.NeonCyan
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500L) // Show splash for 2.5s
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated Logo Placeholder (A glowing 'V')
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ElectricIndigo, NeonCyan)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = DeepSpaceBlack,
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "VELSTRACK",
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 8.sp),
                modifier = Modifier.alpha(alphaAnim.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enterprise Analytics Platform",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                modifier = Modifier.alpha(alphaAnim.value)
            )
        }
    }
}
