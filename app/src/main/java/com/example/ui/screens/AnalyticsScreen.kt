package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import com.example.ui.components.AgentResolutionRateLineChartCanvas
import com.example.ui.components.AgentResolutionRateLineChartCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyResolutionTrend
import com.example.model.SupportAnalyticsSummary

@Composable
fun AnalyticsScreen(
    summary: SupportAnalyticsSummary,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Resolution Trends, 1: Response Times, 2: Combined
    var selectedDayIndex by remember { mutableStateOf<Int?>(summary.dailyTrends.lastIndex) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Recharts Tagline
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B82F6), Color(0xFF10B981))
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Analytics",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Performance Analytics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Recharts Engine",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Daily ticket resolution rates, SLA times & AI deflection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Summary KPI Metrics Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "7-DAY RESOLUTION & SLA OVERVIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalyticsStatCard(
                        title = "Resolved",
                        value = "${summary.resolvedWeek}/${summary.totalTicketsWeek}",
                        subtitle = "${summary.resolutionRatePercent.toInt()}% resolved",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsStatCard(
                        title = "Avg Response",
                        value = "${summary.overallAvgResponseMinutes}m",
                        subtitle = "SLA: < 5.0m",
                        icon = Icons.Default.Speed,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsStatCard(
                        title = "AI Deflection",
                        value = "${summary.aiDeflectionPercent.toInt()}%",
                        subtitle = "Gemini auto",
                        icon = Icons.Default.AutoAwesome,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsStatCard(
                        title = "CSAT",
                        value = "${summary.csatAverage}",
                        subtitle = "Score / 5.0",
                        icon = Icons.Default.Star,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Chart View Selector Tabs
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 4.dp,
                    containerColor = Color.Transparent,
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Resolution Rate", style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ticket Volume", style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Response Times", style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dual Metric", style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }
            }
        }

        // Main Visualizer Canvas Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_chart_container")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Chart Header & Interactive Tooltip State
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Agent 7-Day Resolution Rate (%)"
                                    1 -> "Daily Ticket Volume & Resolution"
                                    2 -> "Average Response Time (Minutes)"
                                    else -> "Combined Resolution vs Response SLA"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Line chart tracking resolution rate trajectory vs 90% SLA target"
                                    else -> "Tap any day on the chart below to inspect detailed metrics"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recharts Legend Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedTab == 0) {
                            LegendItem(color = Color(0xFF10B981), label = "Agent Resolution Rate (%)")
                            LegendItem(color = Color(0xFFEF4444), label = "90% SLA Benchmark")
                        }
                        if (selectedTab == 1 || selectedTab == 3) {
                            LegendItem(color = Color(0xFF94A3B8), label = "Total Tickets")
                            LegendItem(color = Color(0xFF10B981), label = "Resolved")
                            LegendItem(color = Color(0xFF8B5CF6), label = "Gemini Auto")
                        }
                        if (selectedTab == 2 || selectedTab == 3) {
                            LegendItem(color = Color(0xFF3B82F6), label = "Avg Response Time")
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Custom Responsive Canvas Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .testTag("recharts_canvas_box")
                    ) {
                        when (selectedTab) {
                            0 -> AgentResolutionRateLineChartCanvas(
                                data = summary.dailyTrends,
                                selectedIndex = selectedDayIndex,
                                onSelectIndex = { selectedDayIndex = it }
                            )
                            1 -> ResolutionBarChartCanvas(
                                data = summary.dailyTrends,
                                selectedIndex = selectedDayIndex,
                                onSelectIndex = { selectedDayIndex = it }
                            )
                            2 -> ResponseTimeAreaChartCanvas(
                                data = summary.dailyTrends,
                                selectedIndex = selectedDayIndex,
                                onSelectIndex = { selectedDayIndex = it }
                            )
                            3 -> DualMetricCombinedChartCanvas(
                                data = summary.dailyTrends,
                                selectedIndex = selectedDayIndex,
                                onSelectIndex = { selectedDayIndex = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Selected Day Inspector Card (Tooltip equivalent)
                    selectedDayIndex?.let { idx ->
                        if (idx in summary.dailyTrends.indices) {
                            val item = summary.dailyTrends[idx]
                            val isAboveSla = item.agentResolutionRate >= 90.0f
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedTab == 0) {
                                        if (isAboveSla) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFFEF4444).copy(alpha = 0.35f)
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chart_selected_day_tooltip")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${item.dayLabel} (${item.dateStr})",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Text(
                                            text = "${item.agentResolutionRate}% Agent Res.",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAboveSla) Color(0xFF10B981) else Color(0xFFEF4444)
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TooltipDetailItem(
                                            title = "Agent Rate",
                                            value = "${item.agentResolutionRate}%",
                                            color = Color(0xFF10B981)
                                        )
                                        TooltipDetailItem(
                                            title = "Tickets",
                                            value = "${item.resolvedTickets}/${item.totalTickets}",
                                            color = Color(0xFF64748B)
                                        )
                                        TooltipDetailItem(
                                            title = "Gemini AI",
                                            value = "${item.aiAutoResolved}",
                                            color = Color(0xFF8B5CF6)
                                        )
                                        TooltipDetailItem(
                                            title = "Response",
                                            value = "${item.avgResponseMinutes}m",
                                            color = Color(0xFF3B82F6)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Trend Breakdown Table & Breakdown List
        item {
            Text(
                text = "DAILY BREAKDOWN & SLA LOGS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DAY / DATE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.1f)
                        )
                        Text(
                            text = "TICKETS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.9f)
                        )
                        Text(
                            text = "AI DEFLECT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.9f)
                        )
                        Text(
                            text = "AGENT RES.",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.1f)
                        )
                        Text(
                            text = "AVG SLA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    summary.dailyTrends.forEachIndexed { index, trend ->
                        val isSelected = selectedDayIndex == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDayIndex = index }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.1f)) {
                                    Text(
                                        text = trend.dayLabel,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = trend.dateStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(0.9f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${trend.resolvedTickets}/${trend.totalTickets}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(0.9f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${trend.aiAutoResolved}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF8B5CF6)
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(1.1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${trend.agentResolutionRate}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (trend.agentResolutionRate >= 90f) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(0.9f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${trend.avgResponseMinutes}m",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (trend.avgResponseMinutes <= 4.0f) Color(0xFF10B981) else Color(0xFFF59E0B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Operational Recommendations by Gemini AI
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gemini Operational Insights",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Wednesday experienced the peak ticket volume (48 tickets). Gemini AI deflected 67% of repetitive billing queries, maintaining sub-3 minute response times.\n• Friday showed optimal SLA resolution (2.5m avg). Enabling auto-resolve on authentication lockouts further reduces queue wait times by ~22%.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ----------------------------------------------------
// Custom Visualizer Canvas Components (Recharts emulation)
// ----------------------------------------------------

@Composable
fun ResolutionBarChartCanvas(
    data: List<DailyResolutionTrend>,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit
) {
    val maxTickets = (data.maxOfOrNull { it.totalTickets } ?: 50).coerceAtLeast(40).toFloat()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val slotWidth = size.width / data.size
                    val index = (offset.x / slotWidth).toInt().coerceIn(0, data.lastIndex)
                    onSelectIndex(index)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val paddingBottom = 26f
        val chartHeight = height - paddingBottom
        val count = data.size
        val slotWidth = width / count
        val barGroupWidth = slotWidth * 0.55f
        val barWidth = barGroupWidth / 2f

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight * (i.toFloat() / gridLines)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.35f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        }

        // Draw bars for each day
        data.forEachIndexed { index, item ->
            val centerX = index * slotWidth + slotWidth / 2f
            val isSelected = selectedIndex == index

            val totalHeight = (item.totalTickets / maxTickets) * chartHeight
            val resolvedHeight = (item.resolvedTickets / maxTickets) * chartHeight
            val aiHeight = (item.aiAutoResolved / maxTickets) * chartHeight

            // Selection indicator column background
            if (isSelected) {
                drawRoundRect(
                    color = Color(0xFF3B82F6).copy(alpha = 0.10f),
                    topLeft = Offset(centerX - slotWidth / 2f + 4f, 0f),
                    size = Size(slotWidth - 8f, chartHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            // Bar 1: Total Tickets (Grey/Slate)
            val bar1Left = centerX - barGroupWidth / 2f
            val bar1Top = chartHeight - totalHeight
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF64748B), Color(0xFF94A3B8))
                ),
                topLeft = Offset(bar1Left, bar1Top),
                size = Size(barWidth - 4f, totalHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Bar 2: Resolved Tickets (Emerald Green) with stacked AI Deflection (Purple)
            val bar2Left = bar1Left + barWidth
            val bar2Top = chartHeight - resolvedHeight
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF059669))
                ),
                topLeft = Offset(bar2Left, bar2Top),
                size = Size(barWidth - 4f, resolvedHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Stacked top cap for AI Resolved portion inside Bar 2
            if (aiHeight > 0) {
                val aiTop = chartHeight - aiHeight
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
                    ),
                    topLeft = Offset(bar2Left, aiTop),
                    size = Size(barWidth - 4f, aiHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }
    }
}

@Composable
fun ResponseTimeAreaChartCanvas(
    data: List<DailyResolutionTrend>,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit
) {
    val maxTime = (data.maxOfOrNull { it.avgResponseMinutes } ?: 6.0f).coerceAtLeast(5.0f) * 1.15f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val slotWidth = size.width / (data.size - 1).coerceAtLeast(1)
                    val index = ((offset.x + slotWidth / 2) / slotWidth).toInt().coerceIn(0, data.lastIndex)
                    onSelectIndex(index)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val paddingBottom = 26f
        val chartHeight = height - paddingBottom
        val count = data.size
        val stepX = width / (count - 1).coerceAtLeast(1)

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight * (i.toFloat() / gridLines)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.35f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        }

        // SLA threshold line at 4.0m
        val slaY = chartHeight - (4.0f / maxTime) * chartHeight
        drawLine(
            color = Color(0xFFEF4444).copy(alpha = 0.6f),
            start = Offset(0f, slaY),
            end = Offset(width, slaY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
        )

        val points = data.mapIndexed { index, item ->
            val x = index * stepX
            val y = chartHeight - (item.avgResponseMinutes / maxTime) * chartHeight
            Offset(x, y)
        }

        // Draw Smooth Area Gradient
        val areaPath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, chartHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, chartHeight)
                close()
            }
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3B82F6).copy(alpha = 0.40f),
                    Color(0xFF3B82F6).copy(alpha = 0.05f)
                ),
                startY = 0f,
                endY = chartHeight
            )
        )

        // Draw Stroke Line
        val linePath = Path().apply {
            points.forEachIndexed { i, p ->
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
        }

        drawPath(
            path = linePath,
            color = Color(0xFF2563EB),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        // Draw Data Points and Selection Highlight
        points.forEachIndexed { index, p ->
            val isSelected = selectedIndex == index
            drawCircle(
                color = if (isSelected) Color(0xFF1D4ED8) else Color.White,
                radius = if (isSelected) 8f else 5f,
                center = p
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = if (isSelected) 8f else 5f,
                center = p,
                style = Stroke(width = if (isSelected) 3.5f else 2.5f)
            )

            // Vertical indicator on selected day
            if (isSelected) {
                drawLine(
                    color = Color(0xFF2563EB).copy(alpha = 0.6f),
                    start = Offset(p.x, 0f),
                    end = Offset(p.x, chartHeight),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }
        }
    }
}

@Composable
fun DualMetricCombinedChartCanvas(
    data: List<DailyResolutionTrend>,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit
) {
    val maxTickets = (data.maxOfOrNull { it.totalTickets } ?: 50).coerceAtLeast(40).toFloat()
    val maxTime = (data.maxOfOrNull { it.avgResponseMinutes } ?: 6.0f).coerceAtLeast(5.0f) * 1.15f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val slotWidth = size.width / data.size
                    val index = (offset.x / slotWidth).toInt().coerceIn(0, data.lastIndex)
                    onSelectIndex(index)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val paddingBottom = 26f
        val chartHeight = height - paddingBottom
        val count = data.size
        val slotWidth = width / count
        val barWidth = slotWidth * 0.35f

        // Grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight * (i.toFloat() / gridLines)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )
        }

        // Draw Bars (Resolved Tickets)
        data.forEachIndexed { index, item ->
            val centerX = index * slotWidth + slotWidth / 2f
            val resolvedHeight = (item.resolvedTickets / maxTickets) * chartHeight
            val barTop = chartHeight - resolvedHeight

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF10B981), Color(0xFF059669))
                ),
                topLeft = Offset(centerX - barWidth / 2f, barTop),
                size = Size(barWidth, resolvedHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Draw Line on top (Avg Response Time)
        val linePoints = data.mapIndexed { index, item ->
            val x = index * slotWidth + slotWidth / 2f
            val y = chartHeight - (item.avgResponseMinutes / maxTime) * chartHeight
            Offset(x, y)
        }

        val linePath = Path().apply {
            linePoints.forEachIndexed { i, p ->
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
        }

        drawPath(
            path = linePath,
            color = Color(0xFF3B82F6),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        linePoints.forEachIndexed { index, p ->
            val isSelected = selectedIndex == index
            drawCircle(
                color = Color.White,
                radius = if (isSelected) 7f else 4.5f,
                center = p
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = if (isSelected) 7f else 4.5f,
                center = p,
                style = Stroke(width = 2.5f)
            )
        }
    }
}

// ----------------------------------------------------
// UI Helpers
// ----------------------------------------------------

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TooltipDetailItem(title: String, value: String, color: Color) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = color
            )
        }
    }
}
