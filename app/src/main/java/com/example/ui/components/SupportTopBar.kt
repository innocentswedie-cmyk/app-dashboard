package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.AgentProfile
import com.example.model.AgentStatus

@Composable
fun SupportTopBar(
    profile: AgentProfile,
    unreadNotificationsCount: Int,
    onStatusChange: (AgentStatus) -> Unit,
    onOpenSecurityModal: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "SupportPulse",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "SupportPulse",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.extension} • Tier 2",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Actions: Status Pill, Security Shield, Notification Bell, Agent Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Pill with Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { statusMenuExpanded = true }
                                .testTag("agent_status_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val dotColor = when (profile.status) {
                                    AgentStatus.AVAILABLE -> Color(0xFF10B981)
                                    AgentStatus.ON_CALL -> Color(0xFF3B82F6)
                                    AgentStatus.AWAY -> Color(0xFFF59E0B)
                                    AgentStatus.DO_NOT_DISTURB -> Color(0xFFEF4444)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(dotColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = profile.status.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = { statusMenuExpanded = false }
                        ) {
                            AgentStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.label) },
                                    onClick = {
                                        onStatusChange(status)
                                        statusMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Analytics Menu button
                    IconButton(
                        onClick = onOpenAnalytics,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("topbar_analytics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Support Analytics & Trends",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Security Vault button
                    IconButton(
                        onClick = onOpenSecurityModal,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("security_shield_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Sensitive Info & Key Security",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Notifications Bell with Badge
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text(unreadNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Support Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Agent Avatar Initials
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onOpenProfile() }
                            .testTag("profile_avatar_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = profile.name.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
