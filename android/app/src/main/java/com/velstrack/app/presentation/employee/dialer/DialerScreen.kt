package com.velstrack.app.presentation.employee.dialer

import android.Manifest
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.velstrack.app.core.theme.DeepSpaceBlack
import com.velstrack.app.core.theme.NeonCyan
import com.velstrack.app.core.theme.RoseDanger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(
    onNavigateBack: () -> Unit,
    onCallEnded: () -> Unit // Trigger sync after call
) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && phoneNumber.isNotEmpty()) {
            val prefs = context.getSharedPreferences("velstrack_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("pending_call_number", phoneNumber)
                .apply()
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            context.startActivity(intent)
            onCallEnded()
        }
    }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri = result.data?.data ?: return@rememberLauncherForActivityResult
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            context.contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val rawNumber = cursor.getString(numberIndex)
                    phoneNumber = rawNumber.replace(Regex("[^0-9+*#]"), "")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dialer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceBlack,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSpaceBlack)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (phoneNumber.isEmpty()) "Enter Number" else phoneNumber,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (phoneNumber.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                if (phoneNumber.isNotEmpty()) {
                    TextButton(onClick = { phoneNumber = "" }) {
                        Text("Clear All", color = RoseDanger)
                    }
                }
            }

            // Numpad Area
            val keys = listOf(
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "*", "0", "#"
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                items(keys.size) { index ->
                    DialerKey(text = keys[index]) {
                        if (phoneNumber.length < 15) {
                            phoneNumber += keys[index]
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons Layer 1 (Paste & Contacts)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    if (clipboard.hasPrimaryClip()) {
                        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                        if (!text.isNullOrEmpty()) {
                            phoneNumber = text.replace(Regex("[^0-9+*#]"), "")
                        }
                    }
                }) {
                    Text("Paste", color = NeonCyan)
                }
                
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                    contactPickerLauncher.launch(intent)
                }) {
                    Icon(Icons.Default.Person, contentDescription = "Contacts", modifier = Modifier.size(20.dp), tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contacts", color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Layer 2 (Call & Backspace)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(64.dp)) // Padding for alignment

                // Call Button
                FloatingActionButton(
                    onClick = {
                        if (phoneNumber.isNotEmpty()) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                val prefs = context.getSharedPreferences("velstrack_prefs", Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putString("pending_call_number", phoneNumber)
                                    .apply()
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                                context.startActivity(intent)
                                onCallEnded()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                            }
                        }
                    },
                    containerColor = NeonCyan,
                    contentColor = DeepSpaceBlack,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(36.dp))
                }

                // Backspace Button
                IconButton(
                    onClick = {
                        if (phoneNumber.isNotEmpty()) {
                            phoneNumber = phoneNumber.dropLast(1)
                        }
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Backspace", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DialerKey(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
