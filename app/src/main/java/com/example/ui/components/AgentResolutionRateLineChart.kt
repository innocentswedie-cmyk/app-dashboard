package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyResolutionTrend

@Composable
fun AgentResolutionRateLineChartCard(
    dailyTrends: List<DailyResolutionTrend>,
    modifier: Modifier = Modifier,
    title: String = "Agent 7-Day Resolution Rate",
    subtitle: String = "Line chart visualization over past 7 calendar days",
    initialSelectedIndex: Int? = dailyTrends.lastIndex.takeIf { it >= 0 }
) {
    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }

    val avgResolutionRate = remember(dailyTrends) {
        if (dailyTrends.isNotEmpty()) {
            (dailyTrends.map { it.agentResolutionRate }.average() * 10).toInt() / 10f
        } else {
            0f
        }
    }

    val peakDay = remember(dailyTrends) {
        dailyTrends.maxByOrNull { it.agentResolutionRate }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("agent_resolution_line_chart_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Resolution Rate Line Chart",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$avgResolutionRate% Avg",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Badges Row: Average, Peak Day, SLA Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "7-Day Avg",
                    value = "$avgResolutionRate%",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Peak Day",
                    value = peakDay?.let { "${it.dayLabel} (${it.agentResolutionRate.toInt()}%)" } ?: "Fri (97%)",
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1.2f)
                )
                MetricPill(
                    label = "SLA Target",
                    value = "≥ 90.0%",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(3.dp)
                            .background(Color(0xFF10B981), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Resolution Rate (%)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.7f), RoundedCornerShape(1.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "90% SLA Goal",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = Color(0xFFDC2626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The Interactive Line Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .testTag("agent_resolution_canvas_box")
            ) {
                AgentResolutionRateLineChartCanvas(
                    data = dailyTrends,
                    selectedIndex = selectedIndex,
                    onSelectIndex = { selectedIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Day Selector Pills (Mon - Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyTrends.forEachIndexed { index, trend ->
                    val isSelected = selectedIndex == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { selectedIndex = index }
                            .testTag("day_chip_${trend.dayLabel}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trend.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${trend.agentResolutionRate.toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                else Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Day Inspection Tooltip Box
            selectedIndex?.let { idx ->
                if (idx in dailyTrends.indices) {
                    val item = dailyTrends[idx]
                    val isAboveSla = item.agentResolutionRate >= 90.0f
                    val delta = item.agentResolutionRate - 90.0f
                    val deltaText = if (delta >= 0) "+${(delta * 10).toInt() / 10f}% vs SLA" else "${(delta * 10).toInt() / 10f}% vs SLA"

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAboveSla) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("line_chart_day_inspector")
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
                                        text = "${item.dayLabel}, ${item.dateStr}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isAboveSla) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = deltaText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAboveSla) Color(0xFF059669) else Color(0xFFDC2626)
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Resolution Rate",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${item.agentResolutionRate}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Resolved / Total",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${item.resolvedTickets} of ${item.totalTickets}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Gemini Deflected",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${item.aiAutoResolved} tickets",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF8B5CF6)
                                        )
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Avg Response",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${item.avgResponseMinutes}m",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3B82F6)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgentResolutionRateLineChartCanvas(
    data: List<DailyResolutionTrend>,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    // Dynamic bounds: between 80% and 100%
    val minRate = 80f
    val maxRate = 100f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val stepX = size.width / (data.size - 1).coerceAtLeast(1)
                    val index = ((offset.x + stepX / 2f) / stepX).toInt().coerceIn(0, data.lastIndex)
                    onSelectIndex(index)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val paddingBottom = 28f
        val chartHeight = height - paddingBottom
        val count = data.size
        val stepX = width / (count - 1).coerceAtLeast(1)

        // Draw horizontal grid lines (80%, 85%, 90%, 95%, 100%)
        val gridPercentSteps = listOf(80f, 85f, 90f, 95f, 100f)
        gridPercentSteps.forEach { pct ->
            val y = chartHeight - ((pct - minRate) / (maxRate - minRate)) * chartHeight
            val isTarget = (pct == 90f)

            drawLine(
                color = if (isTarget) Color(0xFFEF4444).copy(alpha = 0.55f) else Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = if (isTarget) 1.8f else 1f,
                pathEffect = PathEffect.dashPathEffect(if (isTarget) floatArrayOf(10f, 6f) else floatArrayOf(6f, 6f))
            )
        }

        // Compute points for the 7 days
        val points = data.mapIndexed { index, item ->
            val x = index * stepX
            val rateClamped = item.agentResolutionRate.coerceIn(minRate, maxRate)
            val y = chartHeight - ((rateClamped - minRate) / (maxRate - minRate)) * chartHeight
            Offset(x, y)
        }

        // Draw Gradient Area under the line (Spline/Curved area)
        val areaPath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, chartHeight)
                points.forEachIndexed { i, p ->
                    if (i == 0) {
                        lineTo(p.x, p.y)
                    } else {
                        val prev = points[i - 1]
                        val cx1 = (prev.x + p.x) / 2f
                        val cy1 = prev.y
                        val cx2 = (prev.x + p.x) / 2f
                        val cy2 = p.y
                        cubicTo(cx1, cy1, cx2, cy2, p.x, p.y)
                    }
                }
                lineTo(points.last().x, chartHeight)
                close()
            }
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF10B981).copy(alpha = 0.38f),
                    Color(0xFF10B981).copy(alpha = 0.04f)
                ),
                startY = 0f,
                endY = chartHeight
            )
        )

        // Draw the Smooth Line Path
        val linePath = Path().apply {
            points.forEachIndexed { i, p ->
                if (i == 0) {
                    moveTo(p.x, p.y)
                } else {
                    val prev = points[i - 1]
                    val cx1 = (prev.x + p.x) / 2f
                    val cy1 = prev.y
                    val cx2 = (prev.x + p.x) / 2f
                    val cy2 = p.y
                    cubicTo(cx1, cy1, cx2, cy2, p.x, p.y)
                }
            }
        }

        // Outer glow/shadow stroke
        drawPath(
            path = linePath,
            color = Color(0xFF059669).copy(alpha = 0.25f),
            style = Stroke(width = 7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Main primary stroke
        drawPath(
            path = linePath,
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF10B981))
            ),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Data Points and Selection Highlights
        points.forEachIndexed { index, p ->
            val isSelected = selectedIndex == index
            val item = data[index]

            // Vertical indicator line for selected day
            if (isSelected) {
                drawLine(
                    color = Color(0xFF10B981).copy(alpha = 0.7f),
                    start = Offset(p.x, 0f),
                    end = Offset(p.x, chartHeight),
                    strokeWidth = 1.8f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                )

                // Highlight halo
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.22f),
                    radius = 15f,
                    center = p
                )
            }

            // Outer white fill
            drawCircle(
                color = Color.White,
                radius = if (isSelected) 7.5f else 5.5f,
                center = p
            )

            // Outer border
            drawCircle(
                color = if (isSelected) Color(0xFF047857) else Color(0xFF10B981),
                radius = if (isSelected) 7.5f else 5.5f,
                center = p,
                style = Stroke(width = if (isSelected) 3.5f else 2.5f)
            )

            // Inner dot when selected
            if (isSelected) {
                drawCircle(
                    color = Color(0xFF047857),
                    radius = 3.5f,
                    center = p
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
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
}
