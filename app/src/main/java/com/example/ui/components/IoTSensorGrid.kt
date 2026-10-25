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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnvironmentalSensors
import com.example.data.model.SensorReading
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500

@Composable
fun IoTSensorGrid(
    sensors: EnvironmentalSensors,
    onSensorCardClick: (SensorReading) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // Section Title
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
                        .background(Color(0xFF059669))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Live IoT Environment",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 13.5.sp
                    )
                )
            }
            Text(
                text = "Tap to inspect telemetry",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EnterpriseTextMuted,
                    fontSize = 10.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2x2 Grid Layout
        // Row 1: Air Quality & Ambient Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Air Quality Card
            SensorMetricCard(
                title = "Air Quality",
                primaryValue = "${sensors.aqi} AQI",
                subText = "${sensors.aqiStatus} • PM2.5: ${sensors.pm25} µg",
                badgeText = sensors.aqiStatus,
                badgeColor = Color(0xFF059669),
                badgeBgColor = Color(0xFFECFDF5),
                icon = Icons.Filled.Air,
                accentColor = EnterprisePrimary,
                testTag = "sensor_card_air_quality",
                progressFraction = (sensors.aqi / 100f).coerceIn(0f, 1f),
                modifier = Modifier.weight(1f),
                onClick = {
                    onSensorCardClick(
                        SensorReading(
                            id = "air_quality",
                            title = "Air Quality Index (AQI)",
                            value = "${sensors.aqi} AQI",
                            subValue = "PM2.5: ${sensors.pm25} µg/m³",
                            status = sensors.aqiStatus,
                            isOptimal = sensors.aqi <= 50,
                            details = "Measured via optical particulate laser sensor (Flat B-402 Balcony Node). Temperature: ${sensors.temperatureCelsius}°C."
                        )
                    )
                }
            )

            // 2. Ambient Humidity Card
            SensorMetricCard(
                title = "Ambient Humidity",
                primaryValue = "${sensors.humidityPercentage}%",
                subText = "${sensors.humidityStatus} • Dew: ${sensors.dewPointCelsius}°C",
                badgeText = sensors.humidityStatus,
                badgeColor = EnterpriseBlue,
                badgeBgColor = Color(0xFFEFF6FF),
                icon = Icons.Filled.InvertColors,
                accentColor = EnterpriseBlue,
                testTag = "sensor_card_humidity",
                progressFraction = (sensors.humidityPercentage / 100f).coerceIn(0f, 1f),
                modifier = Modifier.weight(1f),
                onClick = {
                    onSensorCardClick(
                        SensorReading(
                            id = "humidity",
                            title = "Indoor Ambient Humidity",
                            value = "${sensors.humidityPercentage}%",
                            subValue = "Status: ${sensors.humidityStatus}",
                            status = sensors.humidityStatus,
                            isOptimal = sensors.humidityPercentage in 40..65,
                            details = "Capacitive humidity sensor inside living room. Dew point: ${sensors.dewPointCelsius}°C. Comfort rating: Ideal."
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 2: Overhead Water Tank & Piped Gas Leakage Sensor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 3. Overhead Water Tank Level Card
            SensorMetricCard(
                title = "Water Tank Level",
                primaryValue = "${sensors.waterTankLevelPercentage}%",
                subText = "Inflow: ${if (sensors.waterTankInflowActive) "Pump ON" else "Idle"} • ${sensors.waterTankCurrentLitres}L",
                badgeText = if (sensors.waterTankInflowActive) "Inflow Active" else "Stable",
                badgeColor = if (sensors.waterTankInflowActive) Color(0xFF059669) else EnterpriseTextMuted,
                badgeBgColor = if (sensors.waterTankInflowActive) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                icon = Icons.Outlined.Waves,
                accentColor = EnterpriseBlue,
                testTag = "sensor_card_water_tank",
                progressFraction = (sensors.waterTankLevelPercentage / 100f).coerceIn(0f, 1f),
                modifier = Modifier.weight(1f),
                onClick = {
                    onSensorCardClick(
                        SensorReading(
                            id = "water_tank",
                            title = "Overhead Water Tank Level",
                            value = "${sensors.waterTankLevelPercentage}%",
                            subValue = "${sensors.waterTankCurrentLitres} / ${sensors.waterTankCapacityLitres} Litres",
                            status = if (sensors.waterTankInflowActive) "Inflow: Active" else "Idle",
                            isOptimal = sensors.waterTankLevelPercentage >= 50,
                            details = "Ultrasonic level transceiver. Automated solar refill pump is currently active. Inflow rate: 26 L/min."
                        )
                    )
                }
            )

            // 4. Piped Gas Leakage Sensor Card
            SensorMetricCard(
                title = "Piped Gas (PNG)",
                primaryValue = "${String.format("%.2f", sensors.gasLeakagePercentage)}%",
                subText = sensors.gasStatus,
                badgeText = "Safe",
                badgeColor = Color(0xFF059669),
                badgeBgColor = Color(0xFFECFDF5),
                icon = Icons.Filled.LocalGasStation,
                accentColor = Color(0xFF059669),
                testTag = "sensor_card_gas_leakage",
                progressFraction = 0f,
                modifier = Modifier.weight(1f),
                onClick = {
                    onSensorCardClick(
                        SensorReading(
                            id = "gas_sensor",
                            title = "Piped Gas (PNG) Safety Sensor",
                            value = "${String.format("%.2f", sensors.gasLeakagePercentage)}%",
                            subValue = "Pressure: ${sensors.gasPressureMbar} mbar",
                            status = sensors.gasStatus,
                            isOptimal = sensors.gasLeakagePercentage == 0.0,
                            details = "Semiconductor combustible hydrocarbon gas sensor. Valve: ${sensors.gasValveStatus}. Pressure: ${sensors.gasPressureMbar} mbar."
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun SensorMetricCard(
    title: String,
    primaryValue: String,
    subText: String,
    badgeText: String,
    badgeColor: Color,
    badgeBgColor: Color,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    progressFraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header Row: Icon + Badge
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
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Surface(
                    color = badgeBgColor,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeBgColor)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            )

            Text(
                text = primaryValue,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = EnterpriseTextPrimary,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Gauge Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0.04f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EnterpriseTextMuted,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                ),
                maxLines = 1
            )
        }
    }
}
