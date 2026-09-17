package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.model.CustomerInquiry
import com.example.ui.components.ActiveCallConsole
import com.example.ui.components.IncomingCallAlertOverlay
import com.example.ui.components.InquiryDetailDialog
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.SecurityVaultDialog
import com.example.ui.components.SignInProfileDialog
import com.example.ui.components.SupportTopBar
import com.example.ui.screens.AgentProfileScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GeminiChatbotScreen
import com.example.ui.screens.PhoneSystemScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SupportViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SupportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SupportApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SupportApp(viewModel: SupportViewModel) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val filteredInquiries by viewModel.filteredInquiries.collectAsState(initial = emptyList())
    val notifications by viewModel.notifications.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isResolvingInquiry by viewModel.isResolvingInquiry.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val inquiryFilter by viewModel.inquiryFilter.collectAsState()
    val dialpadNumber by viewModel.dialpadNumber.collectAsState()
    val showSignInDialog by viewModel.showSignInDialog.collectAsState()
    val showSecurityModal by viewModel.showSecurityModal.collectAsState()
    val selectedInquiryDetail by viewModel.selectedInquiryDetail.collectAsState()
    val showNotificationSheet by viewModel.showNotificationSheet.collectAsState()
    val analyticsSummary by viewModel.analyticsSummary.collectAsState()

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SupportTopBar(
                profile = currentProfile,
                unreadNotificationsCount = unreadCount,
                onStatusChange = { viewModel.setAgentStatus(it) },
                onOpenSecurityModal = { viewModel.openSecurityModal(true) },
                onOpenNotifications = { viewModel.toggleNotificationSheet(true) },
                onOpenProfile = { viewModel.openSignInDialog(true) },
                onOpenAnalytics = { viewModel.setSelectedTab(4) }
            )
        },
        bottomBar = {
            Column {
                // Active Call Console appears above the navigation bar during an ongoing call
                ActiveCallConsole(
                    call = activeCall,
                    onMuteToggle = { viewModel.toggleMute() },
                    onHoldToggle = { viewModel.toggleHold() },
                    onEndCall = { viewModel.endCall() },
                    onNotesChanged = { viewModel.updateCallNotes(it) }
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("support_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.setSelectedTab(0) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        modifier = Modifier.testTag("nav_tab_dashboard")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.setSelectedTab(1) },
                        icon = { Icon(Icons.Default.Phone, contentDescription = "Phone System") },
                        label = { Text("Phone") },
                        modifier = Modifier.testTag("nav_tab_phone")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.setSelectedTab(2) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Gemini Copilot") },
                        label = { Text("Gemini AI") },
                        modifier = Modifier.testTag("nav_tab_ai")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.setSelectedTab(4) },
                        icon = { Icon(Icons.Default.QueryStats, contentDescription = "Analytics Menu") },
                        label = { Text("Analytics") },
                        modifier = Modifier.testTag("nav_tab_analytics")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.setSelectedTab(3) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Sign In Profile") },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab View
            when (selectedTab) {
                0 -> DashboardScreen(
                    profile = currentProfile,
                    inquiries = filteredInquiries,
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    activeFilter = inquiryFilter,
                    onFilterChange = { viewModel.setInquiryFilter(it) },
                    resolvingInquiryId = isResolvingInquiry,
                    onResolveInquiry = { viewModel.resolveInquiry(it) },
                    onResolveAllPending = { viewModel.resolveAllPendingInquiries() },
                    onSelectInquiry = { viewModel.selectInquiryDetail(it) },
                    onSimulateCall = { viewModel.simulateIncomingCall() },
                    onSimulateTicket = { viewModel.simulateCustomerTicket() },
                    onNavigateToAnalytics = { viewModel.setSelectedTab(4) }
                )

                1 -> PhoneSystemScreen(
                    dialpadNumber = dialpadNumber,
                    onAppendDialpad = { viewModel.appendDialpad(it) },
                    onDeleteDialpad = { viewModel.deleteDialpad() },
                    onClearDialpad = { viewModel.clearDialpad() },
                    onDial = { viewModel.dialCurrentNumber() },
                    onSimulateCall = { viewModel.simulateIncomingCall() },
                    activeCall = activeCall
                )

                2 -> GeminiChatbotScreen(
                    messages = chatMessages,
                    isLoading = isChatLoading,
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    geminiService = viewModel.repository.geminiService
                )

                3 -> AgentProfileScreen(
                    profile = currentProfile,
                    geminiService = viewModel.repository.geminiService,
                    onOpenSignIn = { viewModel.openSignInDialog(true) },
                    onOpenSecurityModal = { viewModel.openSecurityModal(true) },
                    dailyTrends = analyticsSummary.dailyTrends
                )

                4 -> AnalyticsScreen(
                    summary = analyticsSummary
                )
            }

            // Real-Time Incoming Call Alert Overlay (Highest Z-Index, floating banner over any screen)
            IncomingCallAlertOverlay(
                call = activeCall,
                onAccept = { viewModel.acceptCall() },
                onDecline = { viewModel.declineCall() },
                onSendToAiBot = {
                    viewModel.declineCall()
                    viewModel.setSelectedTab(2)
                    viewModel.sendChatMessage("Auto-attendant routed customer Marcus Sterling from Sterling Financial regarding API integration gateway issue.")
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Modal Dialogs & Sheets
    if (showSignInDialog) {
        SignInProfileDialog(
            currentProfile = currentProfile,
            availableProfiles = viewModel.repository.availableProfiles,
            onSelectProfile = { viewModel.switchProfile(it) },
            onSaveProfile = { name, email, dept, ext ->
                viewModel.updateProfile(name, email, dept, ext)
            },
            onDismiss = { viewModel.openSignInDialog(false) }
        )
    }

    if (showSecurityModal) {
        SecurityVaultDialog(
            geminiService = viewModel.repository.geminiService,
            onDismiss = { viewModel.openSecurityModal(false) }
        )
    }

    selectedInquiryDetail?.let { inq ->
        InquiryDetailDialog(
            inquiry = inq,
            isResolving = isResolvingInquiry == inq.id,
            onResolveWithAi = { viewModel.resolveInquiry(inq.id) },
            onDismiss = { viewModel.selectInquiryDetail(null) }
        )
    }

    if (showNotificationSheet) {
        NotificationsSheet(
            notifications = notifications,
            onNotificationClick = { notification ->
                viewModel.markNotificationRead(notification.id)
                if (notification.relatedInquiryId != null) {
                    val inq = filteredInquiries.find { it.id == notification.relatedInquiryId }
                    if (inq != null) {
                        viewModel.selectInquiryDetail(inq)
                        viewModel.toggleNotificationSheet(false)
                    }
                } else if (notification.relatedCallId != null) {
                    viewModel.setSelectedTab(1)
                    viewModel.toggleNotificationSheet(false)
                }
            },
            onClearAll = { viewModel.clearAllNotifications() },
            onDismiss = { viewModel.toggleNotificationSheet(false) }
        )
    }
}

