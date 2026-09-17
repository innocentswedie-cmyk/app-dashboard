package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallState
import com.example.model.CustomerCall

@Composable
fun ActiveCallConsole(
    call: CustomerCall?,
    onMuteToggle: () -> Unit,
    onHoldToggle: () -> Unit,
    onEndCall: () -> Unit,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCallActive = call != null && (call.callState == CallState.ACTIVE || call.callState == CallState.ON_HOLD)

    AnimatedVisibility(
        visible = isCallActive,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        if (call == null) return@AnimatedVisibility

        var showNotesInput by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("active_call_console"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Top bar: Call duration & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val statusColor = if (call.isOnHold) Color(0xFFF59E0B) else Color(0xFF10B981)
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (call.isOnHold) "CALL ON HOLD" else "CONNECTED LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }

                    // Formatted duration
                    val mins = call.durationSeconds / 60
                    val secs = call.durationSeconds % 60
                    val durationText = String.format("%02d:%02d", mins, secs)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Caller info & audio waveform
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = call.callerName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${call.company} • ${call.callerPhone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Audio waveform visualizer bars
                    if (!call.isOnHold && !call.isMuted) {
                        AudioWaveformAnimation()
                    } else {
                        Text(
                            text = if (call.isMuted) "MIC MUTED" else "HOLD MUSIC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (call.isMuted) Color(0xFFEF4444).copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface,
                                CircleShape
                            )
                            .testTag("call_mute_button")
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (call.isMuted) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Hold
                    IconButton(
                        onClick = onHoldToggle,
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (call.isOnHold) Color(0xFFF59E0B).copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface,
                                CircleShape
                            )
                            .testTag("call_hold_button")
                    ) {
                        Icon(
                            imageVector = if (call.isOnHold) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Hold",
                            tint = if (call.isOnHold) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Notes toggle
                    IconButton(
                        onClick = { showNotesInput = !showNotesInput },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (showNotesInput) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface,
                                CircleShape
                            )
                            .testTag("call_notes_button")
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 20.sp
                        )
                    }

                    // End Call
                    Button(
                        onClick = onEndCall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("call_end_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // In-call live notes input
                if (showNotesInput) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = call.notes,
                        onValueChange = onNotesChanged,
                        placeholder = { Text("Enter live call notes or inquiry actions...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("call_notes_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
fun AudioWaveformAnimation() {
    val transition = rememberInfiniteTransition(label = "waveform")

    val h1 by transition.animateFloat(
        initialValue = 6f, targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 18f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 10f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(290, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 22f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h4"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        listOf(h1, h2, h3, h4).forEach { heightVal ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(heightVal.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
        }
    }
}
