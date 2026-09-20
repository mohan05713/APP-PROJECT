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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnvironmentalSensors
import com.example.data.model.SensorReading
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun IoTSensorGrid(
    sensors: EnvironmentalSensors,
    onSensorCardClick: (SensorReading) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Emerald400)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live IoT Environmental Sensors",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Text(
                text = "Tap to inspect",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid Layout
        // Row 1: Air Quality & Ambient Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Air Quality Card
            SensorMetricCard(
                title = "Air Quality",
                primaryValue = "${sensors.aqi} AQI",
                subText = "${sensors.aqiStatus} • PM2.5: ${sensors.pm25} µg/m³",
                badgeText = sensors.aqiStatus,
                badgeColor = Emerald400,
                badgeTextColor = Slate900,
                icon = Icons.Filled.Air,
                accentColor = Emerald400,
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
                badgeColor = Cyan500,
                badgeTextColor = Slate900,
                icon = Icons.Filled.InvertColors,
                accentColor = Cyan500,
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

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Overhead Water Tank & Piped Gas Leakage Sensor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3. Overhead Water Tank Level Card
            SensorMetricCard(
                title = "Water Tank Level",
                primaryValue = "${sensors.waterTankLevelPercentage}%",
                subText = "Inflow: ${if (sensors.waterTankInflowActive) "Active (Pump On)" else "Idle"} • ${sensors.waterTankCurrentLitres}L",
                badgeText = if (sensors.waterTankInflowActive) "Inflow Active" else "Stable",
                badgeColor = if (sensors.waterTankInflowActive) Emerald400 else Slate400,
                badgeTextColor = Slate900,
                icon = Icons.Outlined.Waves,
                accentColor = Color(0xFF38BDF8),
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
                badgeText = "Safe • No Leakage",
                badgeColor = Emerald400,
                badgeTextColor = Slate900,
                icon = Icons.Filled.LocalGasStation,
                accentColor = Emerald400,
                testTag = "sensor_card_gas_leakage",
                progressFraction = 0f, // 0% leakage is safe
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
    badgeTextColor: Color,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    progressFraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = Slate800,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Icon + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = primaryValue,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Gauge Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Slate700)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0.04f, 1f))
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(accentColor.copy(alpha = 0.6f), accentColor)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp
                ),
                maxLines = 2
            )
        }
    }
}
