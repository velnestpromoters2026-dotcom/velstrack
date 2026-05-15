package com.velstrack.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class TimelineItem(
    val title: String,
    val time: String, // Keep original timestamp for calculation
    val duration: String,
    val isHighlighted: Boolean = false,
    val isSynced: Boolean = true
)

@Composable
fun ActivityTimeline(
    items: List<TimelineItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: Timeline dot & Vertical connector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp, start = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.isHighlighted) com.velstrack.app.core.theme.NeonCyan 
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                    )
                    if (index != items.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(48.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            if (item.isHighlighted) com.velstrack.app.core.theme.NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
                
                // CENTER: Contact/number, Relative timestamp, Call type label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call, 
                            contentDescription = "Outgoing", 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatRelativeTime(item.time),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // RIGHT: Duration badge & Sync status icon
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.duration,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = com.velstrack.app.core.theme.NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (item.isSynced) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Synced", style = MaterialTheme.typography.labelSmall, color = com.velstrack.app.core.theme.NeonCyan.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Synced", tint = com.velstrack.app.core.theme.NeonCyan, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
    }
}

fun formatRelativeTime(isoString: String): String {
    try {
        val instant = Instant.parse(isoString)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)
        
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
        
        return when (localDate) {
            today -> "Today • ${localTime.format(timeFormatter)}"
            yesterday -> "Yesterday • ${localTime.format(timeFormatter)}"
            else -> "${localDate.format(dateFormatter)} • ${localTime.format(timeFormatter)}"
        }
    } catch (e: Exception) {
        return "Unknown Time"
    }
}
