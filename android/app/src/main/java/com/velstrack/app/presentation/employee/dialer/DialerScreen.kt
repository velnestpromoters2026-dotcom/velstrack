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

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.app.role.RoleManager
import android.os.Build

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToActiveCall: (String) -> Unit
) {
    var phoneNumberState by remember { mutableStateOf(TextFieldValue("")) }
    val phoneNumber = phoneNumberState.text
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            context.startActivity(intent)
        }
    }

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Whether granted or not, we continue.
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                roleLauncher.launch(intent)
            }
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
                    val formatted = rawNumber.replace(Regex("[^0-9+*#]"), "")
                    phoneNumberState = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
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
                val focusRequester = remember { FocusRequester() }
                
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (phoneNumberState.text.isEmpty()) {
                        Text(
                            text = "Enter Number",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    BasicTextField(
                        value = phoneNumberState,
                        onValueChange = { newValue ->
                            // Only allow valid characters
                            val filtered = newValue.text.replace(Regex("[^0-9+*#]"), "")
                            if (filtered == newValue.text) {
                                phoneNumberState = newValue
                            } else {
                                phoneNumberState = newValue.copy(text = filtered)
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(NeonCyan),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                        // Optionally hide keyboard by making it readOnly? 
                        // If readOnly=true, the cursor can't be moved manually on some devices. 
                        // We will allow soft keyboard to show up if they tap it, 
                        // which enables native paste and cursor movement.
                    )
                }
                
                if (phoneNumberState.text.isNotEmpty()) {
                    TextButton(onClick = { phoneNumberState = TextFieldValue("") }) {
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
                        if (phoneNumberState.text.length < 15) {
                            val currentText = phoneNumberState.text
                            val selectionStart = phoneNumberState.selection.start
                            val selectionEnd = phoneNumberState.selection.end
                            
                            val insertStart = if (selectionStart < 0) currentText.length else selectionStart
                            val insertEnd = if (selectionEnd < 0) currentText.length else selectionEnd

                            val newText = currentText.replaceRange(insertStart, insertEnd, keys[index])
                            val newCursorPos = insertStart + 1
                            
                            phoneNumberState = TextFieldValue(
                                text = newText,
                                selection = TextRange(newCursorPos)
                            )
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
                            val formatted = text.replace(Regex("[^0-9+*#]"), "")
                            
                            val currentText = phoneNumberState.text
                            val selectionStart = phoneNumberState.selection.start
                            val selectionEnd = phoneNumberState.selection.end
                            
                            val insertStart = if (selectionStart < 0) currentText.length else selectionStart
                            val insertEnd = if (selectionEnd < 0) currentText.length else selectionEnd

                            val newText = currentText.replaceRange(insertStart, insertEnd, formatted)
                            val newCursorPos = insertStart + formatted.length
                            
                            phoneNumberState = TextFieldValue(
                                text = newText,
                                selection = TextRange(newCursorPos)
                            )
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
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                                context.startActivity(intent)
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
                        val currentText = phoneNumberState.text
                        val selectionStart = phoneNumberState.selection.start
                        val selectionEnd = phoneNumberState.selection.end

                        if (selectionStart != selectionEnd) {
                            // Delete selection
                            val insertStart = if (selectionStart < 0) currentText.length else selectionStart
                            val insertEnd = if (selectionEnd < 0) currentText.length else selectionEnd
                            val newText = currentText.replaceRange(insertStart, insertEnd, "")
                            phoneNumberState = TextFieldValue(
                                text = newText,
                                selection = TextRange(insertStart)
                            )
                        } else if (selectionStart > 0) {
                            // Delete previous character
                            val newText = currentText.removeRange(selectionStart - 1, selectionStart)
                            phoneNumberState = TextFieldValue(
                                text = newText,
                                selection = TextRange(selectionStart - 1)
                            )
                        } else if (selectionStart < 0 && currentText.isNotEmpty()) {
                            // Fallback if no selection is active
                            val newText = currentText.dropLast(1)
                            phoneNumberState = TextFieldValue(
                                text = newText,
                                selection = TextRange(newText.length)
                            )
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
