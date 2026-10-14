package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyConsumption
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary

enum class ChartFilter {
    BOTH, ELECTRICITY, WATER
}

@Composable
fun WeeklyConsumptionChart(
    weeklyData: List<DailyConsumption>,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableStateOf(6) }
    var activeFilter by remember { mutableStateOf(ChartFilter.BOTH) }

    val selectedDay = weeklyData.getOrNull(selectedDayIndex) ?: weeklyData.last()

    val maxElectricity = (weeklyData.maxOfOrNull { it.electricityKWh } ?: 25.0).coerceAtLeast(25.0)
    val maxWater = (weeklyData.maxOfOrNull { it.waterLitres } ?: 500).coerceAtLeast(500).toDouble()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .testTag("weekly_consumption_chart"),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .animateContentSize()
        ) {
            // Chart Header: Title & Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Chart",
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Weekly Consumption Trends",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "Daily resource distribution",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextMuted,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                // Filter Selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ChartFilterChip(
                        label = "All",
                        selected = activeFilter == ChartFilter.BOTH,
                        onClick = { activeFilter = ChartFilter.BOTH },
                        testTag = "filter_chart_both"
                    )
                    ChartFilterChip(
                        label = "kWh",
                        selected = activeFilter == ChartFilter.ELECTRICITY,
                        onClick = { activeFilter = ChartFilter.ELECTRICITY },
                        testTag = "filter_chart_elec"
                    )
                    ChartFilterChip(
                        label = "Litres",
                        selected = activeFilter == ChartFilter.WATER,
                        onClick = { activeFilter = ChartFilter.WATER },
                        testTag = "filter_chart_water"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selected Day Callout Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedDay.dayOfWeek + if (selectedDayIndex == 6) " (Today)" else "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = EnterpriseTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeFilter != ChartFilter.WATER) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD97706))
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${selectedDay.electricityKWh} kWh",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EnterpriseTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        if (activeFilter != ChartFilter.ELECTRICITY) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EnterpriseBlue)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${selectedDay.waterLitres} L",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EnterpriseTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chart Canvas / Interactive Column Bars (Height: 90dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                // Background grid lines
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                    }
                }

                // Columns for Mon-Sun
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEachIndexed { index, day ->
                        val isSelected = index == selectedDayIndex

                        val elecRatio = (day.electricityKWh / maxElectricity).toFloat().coerceIn(0.08f, 1f)
                        val waterRatio = (day.waterLitres / maxWater).toFloat().coerceIn(0.08f, 1f)

                        val animatedElecHeight by animateFloatAsState(
                            targetValue = elecRatio,
                            animationSpec = tween(500),
                            label = "bar_elec_$index"
                        )
                        val animatedWaterHeight by animateFloatAsState(
                            targetValue = waterRatio,
                            animationSpec = tween(500),
                            label = "bar_water_$index"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSelected) Color(0xFFF1F5F9) else Color.Transparent
                                )
                                .clickable { selectedDayIndex = index }
                                .padding(horizontal = 1.dp, vertical = 2.dp)
                                .testTag("chart_day_$index")
                        ) {
                            // Dual Bars Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Electricity Bar
                                if (activeFilter != ChartFilter.WATER) {
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .fillMaxHeight(animatedElecHeight)
                                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                            .background(
                                                if (isSelected) Color(0xFFD97706) else Color(0xFFF59E0B)
                                            )
                                    )
                                }

                                if (activeFilter == ChartFilter.BOTH) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                }

                                // Water Bar
                                if (activeFilter != ChartFilter.ELECTRICITY) {
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .fillMaxHeight(animatedWaterHeight)
                                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                            .background(
                                                if (isSelected) EnterpriseBlue else EnterpriseBlue.copy(alpha = 0.7f)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            // Day Label
                            Text(
                                text = day.dayOfWeek,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) EnterpriseTextPrimary else EnterpriseTextMuted,
                                    fontSize = 9.5.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFFD97706))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Electricity (kWh)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(EnterpriseBlue)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Water (Litres)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = if (selected) EnterprisePrimary else Color.Transparent,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else EnterpriseTextSecondary,
                fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
