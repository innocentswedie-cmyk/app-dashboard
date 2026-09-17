package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.SupportRepository
import com.example.model.AgentProfile
import com.example.model.AgentStatus
import com.example.model.CustomerInquiry
import com.example.model.InquiryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class SupportViewModel(
    val repository: SupportRepository = SupportRepository()
) : ViewModel() {

    val currentProfile: StateFlow<AgentProfile> = repository.currentProfile
    val activeCall = repository.activeCall
    val rawInquiries = repository.inquiries
    val notifications = repository.notifications
    val chatMessages = repository.chatMessages
    val isResolvingInquiry = repository.isResolvingInquiry
    val isChatLoading = repository.isChatLoading
    val analyticsSummary = repository.analyticsSummary

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _inquiryFilter = MutableStateFlow("ALL")
    val inquiryFilter: StateFlow<String> = _inquiryFilter.asStateFlow()

    private val _dialpadNumber = MutableStateFlow("")
    val dialpadNumber: StateFlow<String> = _dialpadNumber.asStateFlow()

    private val _showSignInDialog = MutableStateFlow(false)
    val showSignInDialog: StateFlow<Boolean> = _showSignInDialog.asStateFlow()

    private val _showSecurityModal = MutableStateFlow(false)
    val showSecurityModal: StateFlow<Boolean> = _showSecurityModal.asStateFlow()

    private val _selectedInquiryDetail = MutableStateFlow<CustomerInquiry?>(null)
    val selectedInquiryDetail: StateFlow<CustomerInquiry?> = _selectedInquiryDetail.asStateFlow()

    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

    // Filtered inquiries list
    val filteredInquiries = combine(
        rawInquiries,
        _searchQuery,
        _inquiryFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.customerName.contains(query, ignoreCase = true) ||
                    item.company.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "PENDING" -> item.status == InquiryStatus.PENDING
                "AI_RESOLVED" -> item.status == InquiryStatus.AI_RESOLVED
                "URGENT" -> item.priority.name == "CRITICAL" || item.priority.name == "HIGH"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setInquiryFilter(filter: String) {
        _inquiryFilter.value = filter
    }

    fun openSignInDialog(show: Boolean) {
        _showSignInDialog.value = show
    }

    fun openSecurityModal(show: Boolean) {
        _showSecurityModal.value = show
    }

    fun selectInquiryDetail(inquiry: CustomerInquiry?) {
        _selectedInquiryDetail.value = inquiry
    }

    fun toggleNotificationSheet(open: Boolean) {
        _showNotificationSheet.value = open
    }

    // Dialpad
    fun appendDialpad(char: String) {
        if (_dialpadNumber.value.length < 15) {
            _dialpadNumber.value += char
        }
    }

    fun deleteDialpad() {
        if (_dialpadNumber.value.isNotEmpty()) {
            _dialpadNumber.value = _dialpadNumber.value.dropLast(1)
        }
    }

    fun clearDialpad() {
        _dialpadNumber.value = ""
    }

    fun dialCurrentNumber() {
        if (_dialpadNumber.value.isNotBlank()) {
            repository.startOutgoingCall(_dialpadNumber.value)
            _dialpadNumber.value = ""
        }
    }

    // Agent profile actions
    fun switchProfile(profile: AgentProfile) {
        repository.switchProfile(profile)
        _showSignInDialog.value = false
    }

    fun updateProfile(name: String, email: String, department: String, extension: String) {
        repository.updateProfile(name, email, department, extension)
    }

    fun setAgentStatus(status: AgentStatus) {
        repository.updateAgentStatus(status)
    }

    // Telephony actions
    fun acceptCall() = repository.acceptCall()
    fun declineCall() = repository.declineCall()
    fun toggleMute() = repository.toggleMute()
    fun toggleHold() = repository.toggleHold()
    fun endCall() = repository.endCall()
    fun updateCallNotes(notes: String) = repository.updateCallNotes(notes)

    // Simulation triggers
    fun simulateIncomingCall() = repository.triggerIncomingCall()
    fun simulateCustomerTicket() = repository.simulateNewCustomerTicket()

    // AI actions
    fun resolveInquiry(id: String) = repository.resolveInquiryWithAi(id)
    fun resolveAllPendingInquiries() = repository.resolveAllPendingInquiries()
    fun sendChatMessage(text: String) = repository.sendChatMessage(text)

    // Notifications
    fun markNotificationRead(id: String) = repository.markNotificationRead(id)
    fun clearAllNotifications() = repository.clearAllNotifications()
}
