package com.velstrack.app.presentation.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    style: TextStyle = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val currentValue by animateIntAsState(
        targetValue = if (animationPlayed) targetValue else 0,
        animationSpec = tween(durationMillis = 1500),
        label = "counter"
    )

    LaunchedEffect(key1 = targetValue) {
        animationPlayed = true
    }

    Text(
        text = "$prefix$currentValue$suffix",
        style = style,
        color = color,
        modifier = modifier
    )
}
