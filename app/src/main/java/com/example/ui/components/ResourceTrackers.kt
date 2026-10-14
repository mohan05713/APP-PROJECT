package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GasMeter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResourceMetrics
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary

@Composable
fun ResourceTrackers(
    metrics: ResourceMetrics,
    onToggleGasMode: () -> Unit = {},
    onSelectCylinderUnit: (Int) -> Unit = {},
    onRequestRefill: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD97706))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Daily Resource Trackers",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 13.5.sp
                    )
                )
            }

            // Region Indicator Badge
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Text(
                    text = if (metrics.isLpgCylinderMode) "TN / South India • LPG Cylinders" else "Piped PNG Supply",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EnterpriseTextMuted,
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 1. Electricity Tracker Card
        val electricityFraction = (metrics.electricityKWh / metrics.electricityLimitKWh).toFloat().coerceIn(0f, 1f)
        val animatedElectricityProgress by animateFloatAsState(
            targetValue = electricityFraction,
            animationSpec = tween(600),
            label = "elec_prog"
        )
        val remainingKWh = (metrics.electricityLimitKWh - metrics.electricityKWh).coerceAtLeast(0.0)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .testTag("electricity_tracker_card"),
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
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
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Electricity",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Electricity Usage",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Daily Allocation: ${metrics.electricityLimitKWh.toInt()} kWh",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EnterpriseTextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Text(
                            text = "${(electricityFraction * 100).toInt()}% Used",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${metrics.electricityKWh}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = " kWh",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EnterpriseTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", remainingKWh)} kWh remaining",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Budget Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedElectricityProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFD97706))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 2: Water & Gas Supply (LPG Cylinders / Piped Pipeline Toggle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Water Consumption Card
            val isWaterHigh = metrics.waterLitres > metrics.waterLimitLitres
            val waterFraction = (metrics.waterLitres.toFloat() / metrics.waterLimitLitres).coerceIn(0f, 1f)

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("water_consumption_card"),
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water",
                                tint = EnterpriseBlue,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Surface(
                            color = if (isWaterHigh) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isWaterHigh) Color(0xFFFECACA) else Color(0xFFA7F3D0)
                            )
                        ) {
                            Text(
                                text = if (isWaterHigh) "HIGH" else "NORMAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isWaterHigh) Color(0xFF991B1B) else Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Water Consumed",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EnterpriseTextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${metrics.waterLitres}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = " Litres",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EnterpriseTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(waterFraction)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(EnterpriseBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Limit: ${metrics.waterLimitLitres} L / day",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextMuted,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }

            // Gas Supply Card (LPG Cylinder Setup vs Piped PNG)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("gas_meter_card"),
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Gas Supply",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Mode Toggle Button (LPG vs PNG)
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.clickable { onToggleGasMode() }
                        ) {
                            Text(
                                text = if (metrics.isLpgCylinderMode) "LPG CYLINDERS ⇅" else "PIPED PNG ⇅",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (metrics.isLpgCylinderMode) {
                        // LPG Cylinder Mode (Tamil Nadu / Chennai / Karnataka Default)
                        val activeUnitObj = metrics.cylinderUnits.find { it.unitNumber == metrics.activeCylinderUnit }
                            ?: metrics.cylinderUnits.firstOrNull()
                            ?: com.example.data.model.GasCylinderUnit(1, "Unit 1")

                        Text(
                            text = "LPG Gas Supply",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EnterpriseTextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${activeUnitObj.remainingPercentage}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeUnitObj.remainingPercentage <= 20) Color(0xFFDC2626) else EnterpriseTextPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = " (${activeUnitObj.unitName})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EnterpriseTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        val cylinderFraction = (activeUnitObj.remainingPercentage / 100f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(cylinderFraction)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (activeUnitObj.remainingPercentage <= 20) Color(0xFFDC2626) else Color(0xFFD97706))
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Unit Selectors: Unit 1 | Unit 2 | Unit 3
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            metrics.cylinderUnits.forEach { unit ->
                                val isSelected = unit.unitNumber == metrics.activeCylinderUnit
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF0F172A) else EnterpriseBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectCylinderUnit(unit.unitNumber) }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = unit.unitName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else EnterpriseTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                        Text(
                                            text = "${unit.remainingPercentage}%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color(0xFFFCD34D) else EnterpriseTextMuted,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 8.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (activeUnitObj.bookingId.isNotBlank()) "Refill: ${activeUnitObj.bookingId}" else activeUnitObj.status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (activeUnitObj.bookingId.isNotBlank()) Color(0xFF2563EB) else EnterpriseTextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Refill ↗",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                modifier = Modifier
                                    .clickable { onRequestRefill(activeUnitObj.unitNumber) }
                                    .padding(start = 2.dp)
                            )
                        }

                    } else {
                        // Piped Gas (PNG) Mode
                        Text(
                            text = "Piped Gas (PNG)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EnterpriseTextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${metrics.gasScm}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = " SCM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EnterpriseTextSecondary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.42f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFD97706))
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Pressure: ${metrics.gasPressureMbar.toInt()} mbar",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextMuted,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
