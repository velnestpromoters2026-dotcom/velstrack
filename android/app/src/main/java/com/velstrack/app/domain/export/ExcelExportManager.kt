package com.velstrack.app.domain.export

import android.content.Context
import android.os.Environment
import android.util.Log
import com.velstrack.app.data.local.dao.CallDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExcelExportManager @Inject constructor(
    private val callDao: CallDao,
    @ApplicationContext private val context: Context
) {
    suspend fun exportCallsToExcel(): String? = withContext(Dispatchers.IO) {
        try {
            val sessions = callDao.getAllVerifiedSessions()
            if (sessions.isEmpty()) return@withContext null
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Velstrack_Calls_$timestamp.xlsx"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            
            val file = File(downloadsDir, fileName)
            val os = FileOutputStream(file)
            
            val wb = Workbook(os, "Velstrack", "1.0")
            val ws = wb.newWorksheet("Call Logs")
            
            ws.value(0, 0, "Employee ID")
            ws.value(0, 1, "Phone Number")
            ws.value(0, 2, "Contact Name")
            ws.value(0, 3, "Date")
            ws.value(0, 4, "Time")
            ws.value(0, 5, "Duration")
            ws.value(0, 6, "Call Type")
            ws.value(0, 7, "Status")
            
            var row = 1
            for (session in sessions) {
                ws.value(row, 0, session.employeeId)
                ws.value(row, 1, session.phoneNumber)
                ws.value(row, 2, session.contactName ?: "Unknown")
                ws.value(row, 3, session.callDate ?: "")
                ws.value(row, 4, session.callTime ?: "")
                
                val duration = session.durationSeconds ?: 0
                val durationStr = when {
                    duration < 60 -> "${duration}s"
                    duration < 3600 -> "${duration / 60}m ${duration % 60}s"
                    else -> "${duration / 3600}h ${(duration % 3600) / 60}m"
                }
                
                ws.value(row, 5, durationStr)
                ws.value(row, 6, session.callType ?: "OUTGOING")
                ws.value(row, 7, session.status)
                row++
            }
            
            wb.finish()
            os.close()
            
            return@withContext file.absolutePath
        } catch (e: Exception) {
            Log.e("ExcelExportManager", "Failed to export Excel", e)
            return@withContext null
        }
    }
}
