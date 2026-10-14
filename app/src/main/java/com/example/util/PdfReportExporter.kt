package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.CommunityNoticeEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.MaintenanceDueItemEntity
import com.example.data.local.UserEntity
import com.example.data.model.DuePaymentStatus
import com.example.data.model.EnvironmentalSensors
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    fun generateAndExportReport(
        context: Context,
        user: UserEntity?,
        sensors: EnvironmentalSensors,
        dues: List<MaintenanceDueItemEntity>,
        complaints: List<ComplaintEntity>,
        notices: List<CommunityNoticeEntity>
    ): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard (595x842 pt)
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(16, 185, 129) // Emerald primary
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(15, 23, 42) // Slate 900
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(71, 85, 105) // Slate 600
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(30, 41, 59)
            textSize = 10f
        }
        val boldTextPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(15, 23, 42)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }
        val accentBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
        }

        var y = 40f
        val leftMargin = 36f
        val rightMargin = 559f

        // Top Header Banner
        canvas.drawRect(leftMargin - 10, y - 20, rightMargin + 10, y + 42, accentBgPaint)
        canvas.drawText("SRM GREEN RESIDENCY • ECOAPARTMENT OS", leftMargin, y + 5, titlePaint)
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Official Facility & Maintenance Summary Report • Generated: $dateStr", leftMargin, y + 25, subHeaderPaint)

        y += 65f

        // 1. Resident & Account Details
        canvas.drawText("1. RESIDENT IDENTITY & UNIT PROFILE", leftMargin, y, headerPaint)
        canvas.drawLine(leftMargin, y + 5, rightMargin, y + 5, linePaint)
        y += 20f

        val flatName = user?.flatNumber ?: "All Residents / General"
        val floorNum = user?.floorNumber?.toString() ?: "N/A"
        val residentName = user?.residentName ?: "SRM Resident"
        val contact = user?.contactNumber ?: "Not Registered"

        canvas.drawText("Resident Name:", leftMargin, y, boldTextPaint)
        canvas.drawText(residentName, leftMargin + 100, y, textPaint)
        canvas.drawText("Flat / Unit:", leftMargin + 280, y, boldTextPaint)
        canvas.drawText(flatName, leftMargin + 360, y, textPaint)
        y += 16f

        canvas.drawText("Floor Number:", leftMargin, y, boldTextPaint)
        canvas.drawText(floorNum, leftMargin + 100, y, textPaint)
        canvas.drawText("Contact Phone:", leftMargin + 280, y, boldTextPaint)
        canvas.drawText(contact, leftMargin + 360, y, textPaint)
        y += 30f

        // 2. Live IoT Environmental & Gas Cylinder Metrics
        canvas.drawText("2. LIVE IOT ENVIRONMENTAL & GAS SUPPLY STATUS", leftMargin, y, headerPaint)
        canvas.drawLine(leftMargin, y + 5, rightMargin, y + 5, linePaint)
        y += 20f

        canvas.drawText("Air Quality (AQI):", leftMargin, y, boldTextPaint)
        canvas.drawText("${sensors.aqi} AQI (${sensors.aqiStatus})", leftMargin + 120, y, textPaint)
        canvas.drawText("Temperature:", leftMargin + 280, y, boldTextPaint)
        canvas.drawText("${sensors.temperatureCelsius}°C", leftMargin + 380, y, textPaint)
        y += 16f

        canvas.drawText("Humidity:", leftMargin, y, boldTextPaint)
        canvas.drawText("${sensors.humidityPercentage}% (${sensors.humidityStatus})", leftMargin + 120, y, textPaint)
        canvas.drawText("Water Tank Level:", leftMargin + 280, y, boldTextPaint)
        canvas.drawText("${sensors.waterTankLevelPercentage}% (${sensors.waterTankCurrentLitres}L / ${sensors.waterTankCapacityLitres}L)", leftMargin + 380, y, textPaint)
        y += 16f

        canvas.drawText("Gas Cylinders (TN):", leftMargin, y, boldTextPaint)
        canvas.drawText("Unit 1: 68% (Active) • Unit 2: 100% (Standby) • Unit 3: 15% (Refill)", leftMargin + 120, y, textPaint)
        y += 30f

        // 3. Maintenance & Dues Section
        canvas.drawText("3. MAINTENANCE DUES & BILLING BREAKDOWN", leftMargin, y, headerPaint)
        canvas.drawLine(leftMargin, y + 5, rightMargin, y + 5, linePaint)
        y += 20f

        if (dues.isNotEmpty()) {
            val totalAmount = dues.sumOf { it.amountRupees }
            dues.take(5).forEach { due ->
                val statusText = if (due.status == DuePaymentStatus.PAID) "[PAID - ${due.paidDate.ifEmpty { "Online" }}]" else "[PENDING DUE]"
                canvas.drawText("• ${due.monthYear} (${due.flatNumber}):", leftMargin, y, boldTextPaint)
                canvas.drawText("₹${due.amountRupees.toInt()}  $statusText  (Due: ${due.dueDate})", leftMargin + 180, y, textPaint)
                y += 16f
            }
            y += 4f
            canvas.drawText("Total Recorded Maintenance Volume: ₹${totalAmount.toInt()}", leftMargin, y, boldTextPaint)
            y += 20f
        } else {
            canvas.drawText("All maintenance dues for this unit are clear.", leftMargin, y, textPaint)
            y += 20f
        }
        y += 10f

        // 4. Recent Maintenance Complaints & Service Tickets
        canvas.drawText("4. FACILITY TICKETS & SERVICE REQUESTS", leftMargin, y, headerPaint)
        canvas.drawLine(leftMargin, y + 5, rightMargin, y + 5, linePaint)
        y += 20f

        if (complaints.isNotEmpty()) {
            complaints.take(4).forEach { comp ->
                canvas.drawText("${comp.ticketId} [${comp.status.name}] - ${comp.category.name.replace('_', ' ')}", leftMargin, y, boldTextPaint)
                y += 14f
                val desc = if (comp.description.length > 70) comp.description.take(70) + "..." else comp.description
                canvas.drawText("Details: $desc", leftMargin + 10, y, textPaint)
                y += 16f
            }
        } else {
            canvas.drawText("No active maintenance tickets raised.", leftMargin, y, textPaint)
            y += 16f
        }
        y += 10f

        // 5. Community Notice Board
        canvas.drawText("5. RECENT COMMUNITY NOTICES & CIRCULARS", leftMargin, y, headerPaint)
        canvas.drawLine(leftMargin, y + 5, rightMargin, y + 5, linePaint)
        y += 20f

        if (notices.isNotEmpty()) {
            notices.take(3).forEach { notice ->
                canvas.drawText("• ${notice.title} [${notice.category}] (${notice.postedTime})", leftMargin, y, boldTextPaint)
                y += 14f
                val desc = if (notice.description.length > 75) notice.description.take(75) + "..." else notice.description
                canvas.drawText("  $desc", leftMargin + 5, y, textPaint)
                y += 16f
            }
        } else {
            canvas.drawText("No circulars posted.", leftMargin, y, textPaint)
            y += 16f
        }

        // Footer
        val footerY = 800f
        canvas.drawLine(leftMargin, footerY - 10, rightMargin, footerY - 10, linePaint)
        canvas.drawText("EcoApartment OS • Smart Green Facility Management • SRM Green Residency B-402", leftMargin, footerY, subHeaderPaint)

        document.finishPage(page)

        return try {
            val reportDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val flatClean = (user?.flatNumber ?: "general").replace(" ", "_").replace("/", "-")
            val file = File(reportDir, "EcoApartment_Report_${flatClean}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    fun shareOrOpenPdf(context: Context, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "EcoApartment Maintenance & Sustainability Report")
                putExtra(Intent.EXTRA_TEXT, "Here is your EcoApartment OS official PDF summary.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Export / Share PDF Report")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Saved PDF to: ${pdfFile.name}", Toast.LENGTH_LONG).show()
        }
    }
}
