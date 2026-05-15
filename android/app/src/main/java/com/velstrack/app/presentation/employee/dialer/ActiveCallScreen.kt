package com.velstrack.app.presentation.employee.dialer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
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
import com.velstrack.app.presentation.employee.dialer.service.CallManager
import android.telecom.Call
import kotlinx.coroutines.delay

@Composable
fun ActiveCallScreen(
    phoneNumber: String,
    onCallEnded: (durationSeconds: Int) -> Unit
) {
    var durationSeconds by remember { mutableStateOf(0) }
    val callStateInt by CallManager.callStateInt.collectAsState()

    LaunchedEffect(callStateInt) {
        if (callStateInt == Call.STATE_DISCONNECTED) {
            onCallEnded(durationSeconds)
        }
    }

    LaunchedEffect(callStateInt) {
        if (callStateInt == Call.STATE_ACTIVE) {
            while (true) {
                delay(1000)
                durationSeconds++
            }
        }
    }

    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
    val statusText = when (callStateInt) {
        Call.STATE_DIALING -> "Dialing..."
        Call.STATE_RINGING -> "Ringing..."
        Call.STATE_ACTIVE -> "Connected"
        Call.STATE_DISCONNECTED -> "Call Ended"
        else -> "Connecting..."
    }

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
                text = statusText,
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
            // End Call
            FloatingActionButton(
                onClick = { 
                    CallManager.endCall() 
                },
                containerColor = RoseDanger,
                contentColor = DeepSpaceBlack,
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "End Call",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
