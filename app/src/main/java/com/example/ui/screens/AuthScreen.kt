package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthState
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.ForjaSecondaryButton
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: ForjaViewModel,
    onAuthSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isRegisterMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showRecoverDialog by remember { mutableStateOf(false) }
    var recoverEmail by remember { mutableStateOf("") }
    var recoverStatusMessage by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showRecoverDialog) {
        AlertDialog(
            onDismissRequest = { showRecoverDialog = false },
            containerColor = ForgeCard,
            title = {
                Text(
                    text = "Recuperação de Acesso",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Informe seu e-mail cadastrado para receber o link de redefinição:",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recoverEmail,
                        onValueChange = { recoverEmail = it },
                        label = { Text("E-mail") },
                        modifier = Modifier.fillMaxWidth().testTag("recover_email_input"),
                        colors = authTextFieldColors()
                    )
                    if (recoverStatusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = recoverStatusMessage!!,
                            color = ForgeGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recoverStatusMessage = viewModel.authRepository.recoverPassword(recoverEmail)
                    },
                    modifier = Modifier.testTag("send_recovery_button")
                ) {
                    Text("Enviar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoverDialog = false }) {
                    Text("Fechar", color = TextSecondaryDark)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ForgeOrange),
                contentAlignment = Alignment.Center
            ) {
                Text("F", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("FORJAGYM", fontWeight = FontWeight.Black, fontSize = 26.sp, color = TextPrimaryDark, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mode switch tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ForgeCard)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isRegisterMode) ForgeOrange else Color.Transparent)
                    .clickable {
                        isRegisterMode = false
                        errorMessage = null
                    }
                    .padding(vertical = 12.dp)
                    .testTag("auth_tab_login"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ENTRAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (!isRegisterMode) Color.White else TextSecondaryDark
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isRegisterMode) ForgeOrange else Color.Transparent)
                    .clickable {
                        isRegisterMode = true
                        errorMessage = null
                    }
                    .padding(vertical = 12.dp)
                    .testTag("auth_tab_register"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CRIAR CONTA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isRegisterMode) Color.White else TextSecondaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form fields
        if (isRegisterMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ForgeOrange) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                colors = authTextFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ForgeOrange) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha (mínimo 6 caracteres)") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ForgeOrange) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Alternar visibilidade",
                        tint = TextSecondaryDark
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
            colors = authTextFieldColors()
        )

        if (!isRegisterMode) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Esqueceu a senha?",
                    color = ForgeOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showRecoverDialog = true }
                        .padding(4.dp)
                        .testTag("forgot_password_link")
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage!!,
                color = ForgeRed,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit button
        ForjaPrimaryButton(
            text = if (isLoading) "PROCESSANDO..." else if (isRegisterMode) "CRIAR MINHA CONTA" else "ENTRAR NO FORJAGYM",
            enabled = !isLoading,
            onClick = {
                isLoading = true
                errorMessage = null
                coroutineScope.launch {
                    val success = if (isRegisterMode) {
                        viewModel.authRepository.register(name, email, password)
                    } else {
                        viewModel.authRepository.login(email, password)
                    }
                    isLoading = false
                    if (success) {
                        onAuthSuccess()
                    } else {
                        val state = viewModel.authState.value
                        if (state is AuthState.Error) {
                            errorMessage = state.message
                        } else {
                            errorMessage = "Falha na autenticação. Verifique os dados."
                        }
                    }
                }
            },
            testTag = "auth_submit_button"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(ForgeBorder))
            Text(
                text = "OU",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(ForgeBorder))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demo login button
        ForjaSecondaryButton(
            text = "⚡ ACESSAR COM MODO DEMO COMPLETO",
            onClick = {
                viewModel.loadDemoMode()
                onAuthSuccess()
            },
            testTag = "auth_demo_button",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Security Notice
        Text(
            text = "🔒 Seus dados e treinos são isolados e protegidos com criptografia. Acesso 100% individual por usuário.",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    focusedLabelColor = ForgeOrange,
    unfocusedLabelColor = TextSecondaryDark,
    cursorColor = ForgeOrange
)
