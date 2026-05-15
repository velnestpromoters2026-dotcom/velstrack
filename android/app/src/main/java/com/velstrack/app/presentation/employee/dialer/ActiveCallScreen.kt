package com.velstrack.app.presentation.employee.dialer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.NeonCyan
import com.velstrack.app.core.theme.RoseDanger
import kotlinx.coroutines.delay

@Composable
fun ActiveCallScreen(
    phoneNumber: String,
    onCallEnded: (durationSeconds: Int) -> Unit
) {
    var durationSeconds by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            durationSeconds++
        }
    }

    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 64.dp)
        ) {
            Text(
                text = "Calling...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = phoneNumber,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timeString,
                color = NeonCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) MaterialTheme.colorScheme.surfaceVariant else DeepSpaceBlack)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }

            // End Call
            FloatingActionButton(
                onClick = { onCallEnded(durationSeconds) },
                containerColor = RoseDanger,
                contentColor = DeepSpaceBlack,
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    modifier = Modifier.size(40.dp)
                )
            }

            // Speaker
            IconButton(
                onClick = { isSpeakerOn = !isSpeakerOn },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isSpeakerOn) MaterialTheme.colorScheme.surfaceVariant else DeepSpaceBlack)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
