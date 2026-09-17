package com.example.model

data class DailyResolutionTrend(
    val dayLabel: String,      // e.g., "Mon", "Tue", "Wed"
    val dateStr: String,       // e.g., "Sep 11"
    val totalTickets: Int,     // Total tickets received
    val resolvedTickets: Int,  // Resolved tickets
    val aiAutoResolved: Int,   // Tickets resolved by Gemini AI
    val avgResponseMinutes: Float, // Average response time in minutes (e.g., 4.2m)
    val agentResolvedTickets: Int = (resolvedTickets * 0.72f).toInt(), // Direct agent handled resolutions
    val agentResolutionRate: Float = if (totalTickets > 0) ((resolvedTickets.toFloat() / totalTickets.toFloat()) * 100f) else 0f
) {
    val resolutionRatePercent: Float
        get() = if (totalTickets > 0) ((resolvedTickets.toFloat() / totalTickets.toFloat()) * 100f) else 0f
}

data class SupportAnalyticsSummary(
    val totalTicketsWeek: Int,
    val resolvedWeek: Int,
    val resolutionRatePercent: Float,
    val overallAvgResponseMinutes: Float,
    val peakHoursString: String,
    val csatAverage: Float,
    val aiDeflectionPercent: Float,
    val agentWeeklyAvgResolutionRate: Float = 94.8f,
    val dailyTrends: List<DailyResolutionTrend>
)
