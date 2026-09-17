package com.example.model

enum class AgentStatus(val label: String) {
    AVAILABLE("Available"),
    ON_CALL("On Call"),
    AWAY("Away"),
    DO_NOT_DISTURB("Do Not Disturb")
}

data class AgentProfile(
    val id: String,
    val name: String,
    val email: String,
    val department: String,
    val extension: String,
    val status: AgentStatus,
    val shiftHours: String = "08:00 - 17:00 EST",
    val callsHandledToday: Int = 18,
    val avgHandleTime: String = "3m 45s",
    val csatScore: Float = 4.92f,
    val fcrRate: String = "94.8%",
    val apiSecurityLevel: String = "Encrypted • BuildConfig"
)

enum class AccountTier(val label: String) {
    ENTERPRISE("Enterprise"),
    VIP("VIP Platinum"),
    GROWTH("Growth"),
    STANDARD("Standard")
}

enum class CallState {
    INCOMING,
    ACTIVE,
    ON_HOLD,
    COMPLETED,
    DECLINED,
    MISSED
}

data class CustomerCall(
    val id: String,
    val callerName: String,
    val callerPhone: String,
    val accountTier: AccountTier,
    val company: String,
    val inquiryTopic: String,
    val callState: CallState,
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isMuted: Boolean = false,
    val isOnHold: Boolean = false,
    val aiSuggestedResolution: String? = null
)

enum class InquiryStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    AI_RESOLVED("AI Resolved"),
    AGENT_RESOLVED("Agent Resolved")
}

enum class InquiryPriority(val label: String) {
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

data class CustomerInquiry(
    val id: String,
    val customerName: String,
    val customerEmail: String,
    val company: String,
    val category: String,
    val priority: InquiryPriority,
    val title: String,
    val description: String,
    val timestamp: Long,
    val status: InquiryStatus,
    val aiResolution: String? = null,
    val sentiment: String = "Inquiring"
)

enum class NotificationType(val label: String) {
    INCOMING_CALL("Incoming Call"),
    MISSED_CALL("Missed Call"),
    URGENT_TICKET("Urgent Ticket"),
    AI_AUTO_RESOLVED("AI Auto-Resolved"),
    VOICEMAIL("Voicemail Transcript")
}

data class SupportNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val summary: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val relatedCallId: String? = null,
    val relatedInquiryId: String? = null
)

enum class ChatSender {
    AGENT,
    AI_BOT,
    SYSTEM
}

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedActions: List<String> = emptyList(),
    val isResolvingInquiry: Boolean = false
)
