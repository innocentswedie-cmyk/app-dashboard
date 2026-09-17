package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerCall

data class CallLogItem(
    val id: String,
    val name: String,
    val phone: String,
    val company: String,
    val type: String, // "INCOMING", "OUTGOING", "MISSED", "VOICEMAIL"
    val duration: String,
    val timeAgo: String,
    val transcript: String? = null
)

@Composable
fun PhoneSystemScreen(
    dialpadNumber: String,
    onAppendDialpad: (String) -> Unit,
    onDeleteDialpad: () -> Unit,
    onClearDialpad: () -> Unit,
    onDial: () -> Unit,
    onSimulateCall: () -> Unit,
    activeCall: CustomerCall?,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Dialpad, 1: Call Log, 2: Voicemail

    val dummyCallLogs = remember {
        listOf(
            CallLogItem("CL-1", "Marcus Sterling", "+1 (555) 839-2041", "Sterling Financial", "MISSED", "0s", "12m ago"),
            CallLogItem("CL-2", "Elena Rostova", "+1 (555) 302-8819", "Internal Escrow", "OUTGOING", "4m 18s", "45m ago"),
            CallLogItem("CL-3", "David Miller", "+1 (555) 749-1102", "Apex Systems", "INCOMING", "6m 32s", "1h ago"),
            CallLogItem("CL-4", "Horizon Retail", "+1 (555) 234-8901", "Horizon Group", "VOICEMAIL", "42s", "2h ago",
                "Transcript: 'Hi, calling regarding duplicate invoice charges on order #8892. Please return our call.'")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("phone_system_screen")
    ) {
        // Telephony Line Status Banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIP TRUNK ONLINE • EXT 402",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF047857)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SRTP AES-256",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sub Tabs: Dialpad vs Call Log vs Voicemail
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Keypad") }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Recent Calls") }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Voicemail") }
            )
        }

        when (selectedSubTab) {
            0 -> DialpadView(
                dialpadNumber = dialpadNumber,
                onAppend = onAppendDialpad,
                onDelete = onDeleteDialpad,
                onDial = onDial,
                onSimulateCall = onSimulateCall
            )
            1 -> CallLogView(
                logs = dummyCallLogs.filter { it.type != "VOICEMAIL" },
                onCallItem = { phone, name ->
                    onClearDialpad()
                    onAppendDialpad(phone)
                    onDial()
                }
            )
            2 -> VoicemailView(
                voicemails = dummyCallLogs.filter { it.type == "VOICEMAIL" },
                onCallBack = { phone, name ->
                    onClearDialpad()
                    onAppendDialpad(phone)
                    onDial()
                }
            )
        }
    }
}

@Composable
fun DialpadView(
    dialpadNumber: String,
    onAppend: (String) -> Unit,
    onDelete: () -> Unit,
    onDial: () -> Unit,
    onSimulateCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Number Display
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (dialpadNumber.isEmpty()) "Enter customer phone..." else dialpadNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (dialpadNumber.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )

                if (dialpadNumber.isNotEmpty()) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Backspace, contentDescription = "Backspace")
                    }
                }
            }
        }

        // Keypad Grid
        val keys = listOf(
            listOf("1" to "", "2" to "ABC", "3" to "DEF"),
            listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
            listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
            listOf("*" to "", "0" to "+", "#" to "")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { (digit, letters) ->
                        DialpadButton(
                            digit = digit,
                            letters = letters,
                            onClick = { onAppend(digit) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Dial Button & Simulation Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick simulation trigger
            Button(
                onClick = onSimulateCall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Incoming Call", style = MaterialTheme.typography.labelSmall)
            }

            // Green Call Button
            Button(
                onClick = onDial,
                enabled = dialpadNumber.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .testTag("dial_call_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Dial",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun DialpadButton(
    digit: String,
    letters: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .size(70.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CallLogView(
    logs: List<CallLogItem>,
    onCallItem: (phone: String, name: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(logs, key = { it.id }) { log ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (log.type) {
                        "INCOMING" -> Icons.Default.CallReceived to Color(0xFF10B981)
                        "OUTGOING" -> Icons.Default.CallMade to Color(0xFF3B82F6)
                        else -> Icons.Default.CallMissed to Color(0xFFEF4444)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(icon.second.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon.first, contentDescription = log.type, tint = icon.second)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = log.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${log.company} • ${log.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${log.duration} • ${log.timeAgo}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onCallItem(log.phone, log.name) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoicemailView(
    voicemails: List<CallLogItem>,
    onCallBack: (phone: String, name: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(voicemails, key = { it.id }) { vm ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Voicemail,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = vm.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = vm.timeAgo,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${vm.company} • ${vm.phone} (${vm.duration})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = vm.transcript ?: "No transcript available",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onCallBack(vm.phone, vm.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Back Customer", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
