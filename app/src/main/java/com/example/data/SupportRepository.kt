package com.example.data

import com.example.model.AccountTier
import com.example.model.AgentProfile
import com.example.model.AgentStatus
import com.example.model.CallState
import com.example.model.ChatMessage
import com.example.model.ChatSender
import com.example.model.CustomerCall
import com.example.model.CustomerInquiry
import com.example.model.DailyResolutionTrend
import com.example.model.InquiryPriority
import com.example.model.InquiryStatus
import com.example.model.NotificationType
import com.example.model.SupportAnalyticsSummary
import com.example.model.SupportNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class SupportRepository(
    val geminiService: GeminiService = GeminiService(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    // Available agent accounts for sign-in / profile switching
    val availableProfiles = listOf(
        AgentProfile(
            id = "AGT-7829",
            name = "Sarah Chen",
            email = "sarah.chen@supportpulse.internal",
            department = "Tier 2 Technical Support & Escalations",
            extension = "Ext. 402",
            status = AgentStatus.AVAILABLE,
            shiftHours = "08:00 - 17:00 EST",
            callsHandledToday = 21,
            avgHandleTime = "3m 42s",
            csatScore = 4.94f,
            fcrRate = "95.1%"
        ),
        AgentProfile(
            id = "AGT-5104",
            name = "Marcus Vance",
            email = "marcus.vance@supportpulse.internal",
            department = "Enterprise VIP Accounts",
            extension = "Ext. 108",
            status = AgentStatus.AVAILABLE,
            shiftHours = "09:00 - 18:00 EST",
            callsHandledToday = 14,
            avgHandleTime = "4m 15s",
            csatScore = 4.88f,
            fcrRate = "92.4%"
        ),
        AgentProfile(
            id = "AGT-9231",
            name = "Elena Rostova",
            email = "elena.rostova@supportpulse.internal",
            department = "Billing & Payments Resolution",
            extension = "Ext. 315",
            status = AgentStatus.AVAILABLE,
            shiftHours = "07:00 - 16:00 EST",
            callsHandledToday = 29,
            avgHandleTime = "2m 58s",
            csatScore = 4.97f,
            fcrRate = "97.0%"
        )
    )

    private val _currentProfile = MutableStateFlow(availableProfiles.first())
    val currentProfile: StateFlow<AgentProfile> = _currentProfile.asStateFlow()

    private val _activeCall = MutableStateFlow<CustomerCall?>(null)
    val activeCall: StateFlow<CustomerCall?> = _activeCall.asStateFlow()

    private val _inquiries = MutableStateFlow<List<CustomerInquiry>>(emptyList())
    val inquiries: StateFlow<List<CustomerInquiry>> = _inquiries.asStateFlow()

    private val _notifications = MutableStateFlow<List<SupportNotification>>(emptyList())
    val notifications: StateFlow<List<SupportNotification>> = _notifications.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isResolvingInquiry = MutableStateFlow<String?>(null)
    val isResolvingInquiry: StateFlow<String?> = _isResolvingInquiry.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _analyticsSummary = MutableStateFlow(
        SupportAnalyticsSummary(
            totalTicketsWeek = 238,
            resolvedWeek = 224,
            resolutionRatePercent = 94.1f,
            overallAvgResponseMinutes = 3.4f,
            peakHoursString = "10:00 - 14:00 EST",
            csatAverage = 4.93f,
            aiDeflectionPercent = 64.7f,
            dailyTrends = emptyList()
        )
    )
    val analyticsSummary: StateFlow<SupportAnalyticsSummary> = _analyticsSummary.asStateFlow()

    private var callTimerJob: Job? = null

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val initialInquiries = listOf(
            CustomerInquiry(
                id = "INQ-4081",
                customerName = "David Miller",
                customerEmail = "david.m@apextech.io",
                company = "Apex Systems",
                category = "Billing",
                priority = InquiryPriority.HIGH,
                title = "Duplicate charge on annual seat renewal",
                description = "We were charged twice on invoice #INV-9281 for 15 user seats on March 15th. Need refund of $749 immediately.",
                timestamp = System.currentTimeMillis() - 1200000,
                status = InquiryStatus.PENDING,
                sentiment = "Frustrated"
            ),
            CustomerInquiry(
                id = "INQ-4082",
                customerName = "Rachel Green",
                customerEmail = "rachel@strataflow.com",
                company = "StrataFlow Labs",
                category = "Authentication",
                priority = InquiryPriority.CRITICAL,
                title = "2FA Lockout after phone upgrade",
                description = "Switched to new device and lost Google Authenticator tokens. Admin login locked out, blocking deployment pipeline.",
                timestamp = System.currentTimeMillis() - 2400000,
                status = InquiryStatus.PENDING,
                sentiment = "Urgent"
            ),
            CustomerInquiry(
                id = "INQ-4083",
                customerName = "Alexander Wright",
                customerEmail = "awright@vanguardlogistics.com",
                company = "Vanguard Logistics",
                category = "API Integration",
                priority = InquiryPriority.MEDIUM,
                title = "Webhooks returning HTTP 429 rate limit",
                description = "Our batch telemetry endpoint hit rate limits during Q3 sync. Requesting temporary quota increase.",
                timestamp = System.currentTimeMillis() - 3600000,
                status = InquiryStatus.AI_RESOLVED,
                aiResolution = "✅ Verified account tier is Enterprise. Applied temporary 2x API rate limit burst for endpoint /v1/telemetry for 48 hours. Advised batching requests to max 50 items.",
                sentiment = "Neutral"
            ),
            CustomerInquiry(
                id = "INQ-4084",
                customerName = "Jessica Taylor",
                customerEmail = "jtaylor@novaprint.co",
                company = "Nova Print Co",
                category = "Shipping & Returns",
                priority = InquiryPriority.MEDIUM,
                title = "Damaged hardware replacement requested",
                description = "Received thermal terminal terminal package with cracked bezel during courier transit. RMA replacement needed.",
                timestamp = System.currentTimeMillis() - 4800000,
                status = InquiryStatus.PENDING,
                sentiment = "Inquiring"
            )
        )
        _inquiries.value = initialInquiries

        val initialNotifications = listOf(
            SupportNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.URGENT_TICKET,
                title = "High Priority Escalation: Apex Systems",
                summary = "David Miller requested immediate review for duplicate billing ($749).",
                timestamp = System.currentTimeMillis() - 600000,
                isRead = false,
                relatedInquiryId = "INQ-4081"
            ),
            SupportNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.AI_AUTO_RESOLVED,
                title = "Gemini Auto-Resolved: Vanguard Logistics",
                summary = "API rate limit automatically adjusted with temporary burst allowance.",
                timestamp = System.currentTimeMillis() - 1800000,
                isRead = true,
                relatedInquiryId = "INQ-4083"
            ),
            SupportNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.VOICEMAIL,
                title = "New Voicemail from +1 (555) 234-8901",
                summary = "Transcript: 'Hi, calling from Horizon Retail regarding invoice #8892. Please call back before 5 PM.'",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = false
            )
        )
        _notifications.value = initialNotifications

        val initialChat = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.SYSTEM,
                text = "Welcome to SupportPulse Copilot! Gemini AI is actively monitoring incoming customer support queues and telephone events.",
                suggestedActions = listOf("Resolve pending inquiries", "Check refund policy", "Security & API status", "Incoming call simulation")
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.AI_BOT,
                text = "Hello Sarah! 3 open customer inquiries are awaiting review. I can automatically generate resolutions for common billing and authentication issues in seconds.",
                suggestedActions = listOf("Auto-Resolve INQ-4081 (Billing)", "Auto-Resolve INQ-4082 (2FA Lock)")
            )
        )
        _chatMessages.value = initialChat

        val initialTrends = listOf(
            DailyResolutionTrend(
                dayLabel = "Mon",
                dateStr = "Sep 11",
                totalTickets = 32,
                resolvedTickets = 30,
                aiAutoResolved = 18,
                avgResponseMinutes = 4.1f,
                agentResolvedTickets = 22,
                agentResolutionRate = 93.8f
            ),
            DailyResolutionTrend(
                dayLabel = "Tue",
                dateStr = "Sep 12",
                totalTickets = 38,
                resolvedTickets = 36,
                aiAutoResolved = 22,
                avgResponseMinutes = 3.6f,
                agentResolvedTickets = 26,
                agentResolutionRate = 94.7f
            ),
            DailyResolutionTrend(
                dayLabel = "Wed",
                dateStr = "Sep 13",
                totalTickets = 48,
                resolvedTickets = 45,
                aiAutoResolved = 32,
                avgResponseMinutes = 2.9f,
                agentResolvedTickets = 33,
                agentResolutionRate = 93.8f
            ),
            DailyResolutionTrend(
                dayLabel = "Thu",
                dateStr = "Sep 14",
                totalTickets = 41,
                resolvedTickets = 39,
                aiAutoResolved = 27,
                avgResponseMinutes = 3.2f,
                agentResolvedTickets = 29,
                agentResolutionRate = 95.1f
            ),
            DailyResolutionTrend(
                dayLabel = "Fri",
                dateStr = "Sep 15",
                totalTickets = 35,
                resolvedTickets = 34,
                aiAutoResolved = 24,
                avgResponseMinutes = 2.5f,
                agentResolvedTickets = 25,
                agentResolutionRate = 97.1f
            ),
            DailyResolutionTrend(
                dayLabel = "Sat",
                dateStr = "Sep 16",
                totalTickets = 22,
                resolvedTickets = 21,
                aiAutoResolved = 16,
                avgResponseMinutes = 3.8f,
                agentResolvedTickets = 15,
                agentResolutionRate = 95.5f
            ),
            DailyResolutionTrend(
                dayLabel = "Sun",
                dateStr = "Sep 17",
                totalTickets = 22,
                resolvedTickets = 20,
                aiAutoResolved = 15,
                avgResponseMinutes = 3.7f,
                agentResolvedTickets = 14,
                agentResolutionRate = 91.0f
            )
        )
        _analyticsSummary.value = SupportAnalyticsSummary(
            totalTicketsWeek = initialTrends.sumOf { it.totalTickets },
            resolvedWeek = initialTrends.sumOf { it.resolvedTickets },
            resolutionRatePercent = (initialTrends.sumOf { it.resolvedTickets }.toFloat() / initialTrends.sumOf { it.totalTickets } * 100f),
            overallAvgResponseMinutes = (initialTrends.map { it.avgResponseMinutes }.average() * 10).toInt() / 10f,
            peakHoursString = "10:00 - 14:00 EST",
            csatAverage = 4.93f,
            aiDeflectionPercent = (initialTrends.sumOf { it.aiAutoResolved }.toFloat() / initialTrends.sumOf { it.resolvedTickets } * 100f),
            agentWeeklyAvgResolutionRate = (initialTrends.map { it.agentResolutionRate }.average() * 10).toInt() / 10f,
            dailyTrends = initialTrends
        )
    }

    // --- Profile & Sign In ---

    fun switchProfile(profile: AgentProfile) {
        _currentProfile.value = profile
    }

    fun updateProfile(name: String, email: String, department: String, extension: String) {
        _currentProfile.update {
            it.copy(name = name, email = email, department = department, extension = extension)
        }
    }

    fun updateAgentStatus(status: AgentStatus) {
        _currentProfile.update { it.copy(status = status) }
    }

    // --- Phone System & Real-Time Telephony ---

    fun triggerIncomingCall(
        callerName: String = "Marcus Sterling",
        callerPhone: String = "+1 (555) 839-2041",
        accountTier: AccountTier = AccountTier.VIP,
        company: String = "Sterling Financial Group",
        inquiryTopic: String = "Urgent Wire Transfer & API Integration Gateway Error"
    ) {
        val call = CustomerCall(
            id = "CALL-" + (1000..9999).random(),
            callerName = callerName,
            callerPhone = callerPhone,
            accountTier = accountTier,
            company = company,
            inquiryTopic = inquiryTopic,
            callState = CallState.INCOMING,
            timestamp = System.currentTimeMillis()
        )
        _activeCall.value = call

        // Add real-time notification
        val notification = SupportNotification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.INCOMING_CALL,
            title = "Incoming Call: $callerName (${accountTier.label})",
            summary = "$company • $inquiryTopic",
            timestamp = System.currentTimeMillis(),
            relatedCallId = call.id
        )
        _notifications.update { listOf(notification) + it }
    }

    fun acceptCall() {
        _activeCall.update { current ->
            current?.copy(callState = CallState.ACTIVE)
        }
        _currentProfile.update { it.copy(status = AgentStatus.ON_CALL) }
        startCallTimer()
    }

    fun declineCall() {
        val current = _activeCall.value
        if (current != null) {
            val missedNotification = SupportNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.MISSED_CALL,
                title = "Missed Call from ${current.callerName}",
                summary = "Call declined or unanswered • ${current.callerPhone}",
                timestamp = System.currentTimeMillis(),
                relatedCallId = current.id
            )
            _notifications.update { listOf(missedNotification) + it }
        }
        stopCallTimer()
        _activeCall.value = null
        _currentProfile.update { it.copy(status = AgentStatus.AVAILABLE) }
    }

    fun toggleMute() {
        _activeCall.update { it?.copy(isMuted = !(it.isMuted)) }
    }

    fun toggleHold() {
        _activeCall.update {
            val nextHold = !(it?.isOnHold ?: false)
            it?.copy(
                isOnHold = nextHold,
                callState = if (nextHold) CallState.ON_HOLD else CallState.ACTIVE
            )
        }
    }

    fun endCall() {
        stopCallTimer()
        _activeCall.update { it?.copy(callState = CallState.COMPLETED) }
        _currentProfile.update {
            it.copy(
                status = AgentStatus.AVAILABLE,
                callsHandledToday = it.callsHandledToday + 1
            )
        }
        _activeCall.value = null
    }

    fun updateCallNotes(notes: String) {
        _activeCall.update { it?.copy(notes = notes) }
    }

    fun startOutgoingCall(phone: String, name: String = "Customer Contact") {
        val call = CustomerCall(
            id = "CALL-" + (1000..9999).random(),
            callerName = name,
            callerPhone = phone,
            accountTier = AccountTier.STANDARD,
            company = "Direct Dial",
            inquiryTopic = "Outbound Follow-up Support Call",
            callState = CallState.ACTIVE,
            timestamp = System.currentTimeMillis()
        )
        _activeCall.value = call
        _currentProfile.update { it.copy(status = AgentStatus.ON_CALL) }
        startCallTimer()
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = scope.launch {
            while (isActive) {
                delay(1000)
                _activeCall.update { call ->
                    if (call != null && (call.callState == CallState.ACTIVE || call.callState == CallState.ON_HOLD)) {
                        call.copy(durationSeconds = call.durationSeconds + 1)
                    } else {
                        call
                    }
                }
            }
        }
    }

    private fun stopCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = null
    }

    // --- Gemini AI Auto-Resolution of Inquiries ---

    fun resolveInquiryWithAi(inquiryId: String) {
        scope.launch {
            val inquiry = _inquiries.value.find { it.id == inquiryId } ?: return@launch
            _isResolvingInquiry.value = inquiryId

            val resolution = geminiService.resolveInquiry(
                title = inquiry.title,
                description = inquiry.description,
                category = inquiry.category,
                customerName = inquiry.customerName
            )

            _inquiries.update { list ->
                list.map { item ->
                    if (item.id == inquiryId) {
                        item.copy(
                            status = InquiryStatus.AI_RESOLVED,
                            aiResolution = resolution
                        )
                    } else item
                }
            }

            // Create resolution notification
            val notification = SupportNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.AI_AUTO_RESOLVED,
                title = "Gemini Resolved: ${inquiry.title}",
                summary = "Automatic solution dispatched to ${inquiry.customerName} (${inquiry.company})",
                timestamp = System.currentTimeMillis(),
                relatedInquiryId = inquiry.id
            )
            _notifications.update { listOf(notification) + it }

            // Add note to AI chat
            val chatMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.AI_BOT,
                text = "✨ **Auto-Resolved Ticket #${inquiry.id}** (${inquiry.title})\n\n$resolution",
                isResolvingInquiry = true
            )
            _chatMessages.update { it + chatMsg }

            _isResolvingInquiry.value = null
        }
    }

    fun resolveAllPendingInquiries() {
        val pending = _inquiries.value.filter { it.status == InquiryStatus.PENDING }
        pending.forEach { inq ->
            resolveInquiryWithAi(inq.id)
        }
    }

    // --- Chatbot Assistant ---

    fun sendChatMessage(userText: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.AGENT,
            text = userText
        )
        _chatMessages.update { it + userMsg }

        scope.launch {
            _isChatLoading.value = true
            val history = _chatMessages.value.map {
                (if (it.sender == ChatSender.AGENT) "user" else "model") to it.text
            }
            val reply = geminiService.chatReply(history, userText)
            
            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.AI_BOT,
                text = reply,
                suggestedActions = listOf("Copy resolution", "Apply to active ticket", "Simulate Incoming Call")
            )
            _chatMessages.update { it + aiMsg }
            _isChatLoading.value = false
        }
    }

    // --- Notifications Management ---

    fun markNotificationRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    // --- Simulation Triggers ---

    fun simulateNewCustomerTicket() {
        val randomNames = listOf("Liam O'Connor", "Sophia Martinez", "Kenji Sato", "Amara Okafor")
        val randomCompanies = listOf("OmniCloud", "Zenith Health", "HyperScale", "Krypton Global")
        val randomCategories = listOf("Billing", "Authentication", "API Integration", "Subscription")
        val randomTitles = listOf(
            "Invoice credit request for maintenance window",
            "SSO SAML certificate rotation required",
            "High latency spikes on European endpoints",
            "Subscription tier transition query"
        )
        val idx = (randomNames.indices).random()

        val newInquiry = CustomerInquiry(
            id = "INQ-" + (5000..9999).random(),
            customerName = randomNames[idx],
            customerEmail = "${randomNames[idx].lowercase().replace(" ", ".")}@company.com",
            company = randomCompanies[idx],
            category = randomCategories[idx],
            priority = InquiryPriority.HIGH,
            title = randomTitles[idx],
            description = "Customer submitted high-priority inquiry regarding ${randomTitles[idx].lowercase()}. Demanding quick response.",
            timestamp = System.currentTimeMillis(),
            status = InquiryStatus.PENDING,
            sentiment = "Urgent"
        )

        _inquiries.update { listOf(newInquiry) + it }

        val notification = SupportNotification(
            id = UUID.randomUUID().toString(),
            type = NotificationType.URGENT_TICKET,
            title = "New Ticket: ${newInquiry.title}",
            summary = "${newInquiry.customerName} (${newInquiry.company}) • ${newInquiry.category}",
            timestamp = System.currentTimeMillis(),
            relatedInquiryId = newInquiry.id
        )
        _notifications.update { listOf(notification) + it }
    }
}
