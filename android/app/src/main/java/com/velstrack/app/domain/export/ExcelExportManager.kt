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
            val calls = callDao.getAllCalls()
            if (calls.isEmpty()) return@withContext null
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Velstrack_Calls_$timestamp.xlsx"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            
            val file = File(downloadsDir, fileName)
            val os = FileOutputStream(file)
            
            val wb = Workbook(os, "Velstrack", "1.0")
            val ws = wb.newWorksheet("Call Logs")
            
            ws.value(0, 0, "Name")
            ws.value(0, 1, "Number")
            ws.value(0, 2, "Date")
            ws.value(0, 3, "Time")
            
            var row = 1
            for (call in calls) {
                ws.value(row, 0, call.contactName)
                ws.value(row, 1, call.clientPhoneHash)
                
                val dateParts = call.readableDate.split(" ")
                val dateStr = if (dateParts.isNotEmpty()) dateParts[0] else ""
                val timeStr = if (dateParts.size > 1) dateParts[1] else ""
                
                ws.value(row, 2, dateStr)
                ws.value(row, 3, timeStr)
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
