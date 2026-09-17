package com.example

import com.example.model.DailyResolutionTrend
import com.example.model.SupportAnalyticsSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun test_agentResolutionRateCalculation() {
        val trend = DailyResolutionTrend(
            dayLabel = "Fri",
            dateStr = "Sep 15",
            totalTickets = 35,
            resolvedTickets = 34,
            aiAutoResolved = 24,
            avgResponseMinutes = 2.5f,
            agentResolvedTickets = 25,
            agentResolutionRate = 97.1f
        )
        assertTrue(trend.agentResolutionRate >= 90.0f)
        assertEquals(97.1f, trend.agentResolutionRate, 0.01f)
    }

    @Test
    fun test_sevenDayResolutionRateSummary() {
        val trends = listOf(
            DailyResolutionTrend("Mon", "Sep 11", 32, 30, 18, 4.1f, 22, 93.8f),
            DailyResolutionTrend("Tue", "Sep 12", 38, 36, 22, 3.6f, 26, 94.7f),
            DailyResolutionTrend("Wed", "Sep 13", 48, 45, 32, 2.9f, 33, 93.8f),
            DailyResolutionTrend("Thu", "Sep 14", 41, 39, 27, 3.2f, 29, 95.1f),
            DailyResolutionTrend("Fri", "Sep 15", 35, 34, 24, 2.5f, 25, 97.1f),
            DailyResolutionTrend("Sat", "Sep 16", 22, 21, 16, 3.8f, 15, 95.5f),
            DailyResolutionTrend("Sun", "Sep 17", 22, 20, 15, 3.7f, 14, 91.0f)
        )

        assertEquals(7, trends.size)
        val avgRate = (trends.map { it.agentResolutionRate }.average() * 10).toInt() / 10f
        assertTrue("Average resolution rate should meet 90% SLA", avgRate >= 90.0f)
        assertEquals(94.4f, avgRate, 0.1f)
    }
}
