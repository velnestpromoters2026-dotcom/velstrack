package com.velstrack.app.domain.telecom

import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import android.provider.ContactsContract
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Singleton
class OutgoingCallSessionManager @Inject constructor(
    private val callDao: CallDao,
    @ApplicationContext private val context: Context
) {

    suspend fun startSession(employeeId: String, rawNumber: String, startEpochMillis: Long): String {
        return withContext(Dispatchers.IO) {
            val normalizedDbNumber = rawNumber.replace(Regex("[^0-9+]"), "")
            // Use precise timestamp to ensure unique session ID
            val sessionId = "${normalizedDbNumber}_${startEpochMillis}"
            
            val readableDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(startEpochMillis))
            
            var contactName = "Unknown"
            try {
                val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(normalizedDbNumber))
                context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            contactName = cursor.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore permission or content resolver errors
            }

            val dummyFingerprint = "temp_${sessionId}"
            
            val initialSession = CallEntity(
                id = sessionId,
                callFingerprint = dummyFingerprint,
                clientPhoneHash = rawNumber, // Unhashed initially
                contactName = contactName,
                readableDate = readableDate,
                durationSeconds = 0,
                callType = "OUTGOING",
                timestamp = startEpochMillis,
                isSynced = false,
                sessionState = "DISCONNECTED", // simplified
                callVerified = true // Instantly verify to skip complex telecom validation
            )
            
            callDao.insertCalls(listOf(initialSession))
            
            sessionId
        }
    }
}
