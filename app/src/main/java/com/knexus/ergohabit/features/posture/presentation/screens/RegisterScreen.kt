package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.RegisterViewModel
import com.knexus.ergohabit.ui.components.ErgoHabitBrandHeader
import com.knexus.ergohabit.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // --- CAMBIO REALIZADO: Observar el éxito del registro para navegar ---
    LaunchedEffect(state.isRegisterSuccess) {
        if (state.isRegisterSuccess) {
            onNavigateToLogin()
            viewModel.resetNavigation()
        }
    }
    // ---------------------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 32.dp)
        ) {

            ErgoHabitBrandHeader(compact = true)

            Spacer(modifier = Modifier.height(20.dp))

            // ── CARD PRINCIPAL ────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ── TABS ──────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F5F0)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tab Iniciar Sesión
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Transparent)
                                .noRippleClickable { onNavigateToLogin() }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextGray
                            )
                        }

                        // Tab Registrarse (activo)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GreenPrimary)
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Registrarse",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── NOMBRE(S) ─────────────────────────────
                    Text(
                        text = "NOMBRE(S) *",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 0.08.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        placeholder = {
                            Text(text = "Ej. Carlos", color = TextGray, fontSize = 14.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = GreenPrimary,
                            unfocusedContainerColor = Color(0xFFF0F5F0),
                            focusedContainerColor = Color(0xFFF0F5F0),
                            cursorColor = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── PRIMER APELLIDO ───────────────────────
                    Text(
                        text = "PRIMER APELLIDO *",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 0.08.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.primerApellido,
                        onValueChange = { viewModel.onPrimerApellidoChange(it) },
                        placeholder = {
                            Text(text = "Ej. López", color = TextGray, fontSize = 14.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = GreenPrimary,
                            unfocusedContainerColor = Color(0xFFF0F5F0),
                            focusedContainerColor = Color(0xFFF0F5F0),
                            cursorColor = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── SEGUNDO APELLIDO ──────────────────────
                    Text(
                        text = "SEGUNDO APELLIDO (OPCIONAL)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 0.08.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.segundoApellido,
                        onValueChange = { viewModel.onSegundoApellidoChange(it) },
                        placeholder = {
                            Text(text = "Ej. García", color = TextGray, fontSize = 14.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = GreenPrimary,
                            unfocusedContainerColor = Color(0xFFF0F5F0),
                            focusedContainerColor = Color(0xFFF0F5F0),
                            cursorColor = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── CORREO ────────────────────────────────
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 0.08.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholder = {
                            Text(text = "tu@email.com", color = TextGray, fontSize = 14.sp)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = GreenPrimary,
                            unfocusedContainerColor = Color(0xFFF0F5F0),
                            focusedContainerColor = Color(0xFFF0F5F0),
                            cursorColor = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── CONTRASEÑA ────────────────────────────
                    Text(
                        text = "CONTRASEÑA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 0.08.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = {
                            Text(text = "••••••••", color = TextGray, fontSize = 14.sp)
                        },
                        singleLine = true,
                        visualTransformation = if (state.passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onTogglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.passwordVisible)
                                        Icons.Outlined.Visibility
                                    else
                                        Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGray
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = GreenPrimary,
                            unfocusedContainerColor = Color(0xFFF0F5F0),
                            focusedContainerColor = Color(0xFFF0F5F0),
                            cursorColor = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CAMBIO REALIZADO: Mostrar mensaje de error si existe ---
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    // ------------------------------------------------------------

                    // ── BOTÓN CREAR CUENTA ────────────────────
                    Button(
                        onClick = {
                            // --- CAMBIO REALIZADO: Solo llamar a crear cuenta, la navegación se maneja en el LaunchedEffect ---
                            viewModel.onCrearCuenta()
                            // ---------------------------------------------------------------------------------------------
                        },
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Crear Cuenta",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}