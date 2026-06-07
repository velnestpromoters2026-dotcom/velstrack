package com.velstrack.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.painterResource
import com.velstrack.app.R
import com.velstrack.app.core.theme.AbsoluteBlack
import com.velstrack.app.core.theme.GlassBorder
import com.velstrack.app.core.theme.GlassSurface
import com.velstrack.app.core.theme.PureWhite
import com.velstrack.app.core.theme.MetallicSilver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isCheckingUpdate by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).role)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsoluteBlack)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // New Metallic Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.velstrack_metallic_logo),
                contentDescription = "Velstrack Logo",
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Massive Header
            Text(
                text = "Hello there,\nWelcome",
                color = PureWhite,
                style = MaterialTheme.typography.displayLarge,
                lineHeight = 60.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sign in to access your workspace.",
                color = MetallicSilver,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = GlassBorder,
                    containerColor = GlassSurface,
                    focusedLabelColor = PureWhite,
                    unfocusedLabelColor = MetallicSilver
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "HIDE" else "SHOW",
                            color = PureWhite,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = GlassBorder,
                    containerColor = GlassSurface,
                    focusedLabelColor = PureWhite,
                    unfocusedLabelColor = MetallicSilver
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = PureWhite)
            } else {
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = AbsoluteBlack
                    )
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isCheckingUpdate) {
                CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
            } else {
                TextButton(
                    onClick = {
                        isCheckingUpdate = true
                        viewModel.checkForUpdates { updateFound ->
                            isCheckingUpdate = false
                            if (updateFound) {
                                Toast.makeText(context, "Update found! Downloading...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "App is up to date", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Check for Updates",
                        color = MetallicSilver,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
