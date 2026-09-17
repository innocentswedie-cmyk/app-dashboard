package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationType
import com.example.model.SupportNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    notifications: List<SupportNotification>,
    onNotificationClick: (SupportNotification) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeFilter by remember { mutableStateOf("ALL") }

    val filteredList = remember(notifications, activeFilter) {
        when (activeFilter) {
            "CALLS" -> notifications.filter { it.type == NotificationType.INCOMING_CALL || it.type == NotificationType.MISSED_CALL || it.type == NotificationType.VOICEMAIL }
            "URGENT" -> notifications.filter { it.type == NotificationType.URGENT_TICKET }
            "AI" -> notifications.filter { it.type == NotificationType.AI_AUTO_RESOLVED }
            else -> notifications
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("notifications_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Real-Time Support Feed",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (notifications.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeFilter == "ALL",
                    onClick = { activeFilter = "ALL" },
                    label = { Text("All (${notifications.size})") }
                )
                FilterChip(
                    selected = activeFilter == "CALLS",
                    onClick = { activeFilter = "CALLS" },
                    label = { Text("Calls") }
                )
                FilterChip(
                    selected = activeFilter == "URGENT",
                    onClick = { activeFilter = "URGENT" },
                    label = { Text("Urgent") }
                )
                FilterChip(
                    selected = activeFilter == "AI",
                    onClick = { activeFilter = "AI" },
                    label = { Text("AI Resolved") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications at this time.\nAll support systems nominal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        NotificationCard(item = item, onClick = { onNotificationClick(item) })
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: SupportNotification,
    onClick: () -> Unit
) {
    val (icon, iconColor, bgColor) = when (item.type) {
        NotificationType.INCOMING_CALL -> Triple(Icons.Default.Call, Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.12f))
        NotificationType.MISSED_CALL -> Triple(Icons.Default.CallMissed, Color(0xFFEF4444), Color(0xFFEF4444).copy(alpha = 0.12f))
        NotificationType.URGENT_TICKET -> Triple(Icons.Default.Warning, Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.12f))
        NotificationType.AI_AUTO_RESOLVED -> Triple(Icons.Default.AutoAwesome, Color(0xFF3B82F6), Color(0xFF3B82F6).copy(alpha = 0.12f))
        NotificationType.VOICEMAIL -> Triple(Icons.Default.Voicemail, Color(0xFF8B5CF6), Color(0xFF8B5CF6).copy(alpha = 0.12f))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.type.label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
