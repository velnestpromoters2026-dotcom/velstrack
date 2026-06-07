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
import androidx.compose.ui.res.painterResource
import com.velstrack.app.R
import com.velstrack.app.core.theme.AbsoluteBlack
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
            .background(AbsoluteBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // New Metallic Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.velstrack_metallic_logo),
                contentDescription = "Velstrack Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            )
            
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
