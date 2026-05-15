package com.velstrack.app.presentation.employee.dialer

import android.telecom.Call
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.presentation.employee.dialer.service.CallManager
import kotlinx.coroutines.delay

@Composable
fun ActiveCallScreen(
    phoneNumber: String,
    onCallEnded: (durationSeconds: Int) -> Unit
) {
    var durationSeconds by remember { mutableStateOf(0) }
    val callStateInt by CallManager.callStateInt.collectAsState()

    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    LaunchedEffect(callStateInt) {
        if (callStateInt == Call.STATE_DISCONNECTED) {
            onCallEnded(durationSeconds)
        }
    }

    LaunchedEffect(callStateInt) {
        if (callStateInt == Call.STATE_ACTIVE || callStateInt == Call.STATE_DISCONNECTED) {
            while (true) {
                delay(1000)
                durationSeconds++
            }
        } else if (callStateInt == 0 && durationSeconds == 0) { // Fallback if system UI intercepts
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
        Call.STATE_ACTIVE -> timeString
        Call.STATE_DISCONNECTED -> "Call Ended"
        else -> timeString // Fallback show timer
    }

    // iOS Style Gradient Background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E3C40), // Dark Teal
            Color(0xFF2A5C61), // Teal
            Color(0xFF11293A)  // Dark Blue
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP: Status & Number ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = statusText,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = phoneNumber,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            // --- MIDDLE: Translucent Transcript ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fake Translation Bubble 1
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Translation is on for this call",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fake Transcript Bubble 2
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Hello, will you be available in December for a photo?",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hi, are you available to cater a wedding on December 6?",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // --- BOTTOM: Controls Grid ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallControlButton(
                        icon = Icons.Rounded.VolumeUp,
                        label = "Speaker",
                        isActive = isSpeakerOn,
                        onClick = { isSpeakerOn = !isSpeakerOn }
                    )
                    CallControlButton(
                        icon = Icons.Rounded.Videocam,
                        label = "FaceTime",
                        isActive = false,
                        onClick = { }
                    )
                    CallControlButton(
                        icon = Icons.Rounded.MicOff,
                        label = "Mute",
                        isActive = isMuted,
                        onClick = { isMuted = !isMuted }
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallControlButton(
                        icon = Icons.Rounded.MoreHoriz,
                        label = "More",
                        isActive = false,
                        onClick = { }
                    )
                    
                    // End Call Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = { CallManager.endCall() },
                            containerColor = Color(0xFFFF3B30), // iOS Red
                            contentColor = Color.White,
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CallEnd,
                                contentDescription = "End Call",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("End", color = Color.White, fontSize = 14.sp)
                    }

                    CallControlButton(
                        icon = Icons.Rounded.Dialpad,
                        label = "Keypad",
                        isActive = false,
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) Color.White else Color.White.copy(alpha = 0.15f)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) DeepSpaceBlack else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
