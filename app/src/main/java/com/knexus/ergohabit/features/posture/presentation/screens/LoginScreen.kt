package com.knexus.ergohabit.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knexus.ergohabit.features.posture.presentation.viewmodel.AuthViewModel
import com.knexus.ergohabit.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Iniciar Sesión, 1 = Registrarse

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
                .padding(horizontal = 32.dp)
        ) {

            // ── LOGO ─────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(GreenPrimary)
            ) {
                Text(text = "🌿", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── NOMBRE APP ───────────────────────────────────
            Text(
                text = "ErgoHabit",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = "Hábitos saludables para estudiantes",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // ── CARD PRINCIPAL ────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ── TABS: Iniciar Sesión / Registrarse ────
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
                                .background(if (selectedTab == 0) GreenPrimary else Color.Transparent)
                                .noRippleClickable { selectedTab = 0 }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == 0) Color.White else TextGray
                            )
                        }

                        // Tab Registrarse
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTab == 1) GreenPrimary else Color.Transparent)
                                .noRippleClickable {
                                    selectedTab = 1
                                    onNavigateToRegister()
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Registrarse",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == 1) Color.White else TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── CAMPO CORREO ──────────────────────────
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

                    // ── CAMPO CONTRASEÑA ──────────────────────
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

                    // ── BOTÓN INICIAR SESIÓN ──────────────────
                    Button(
                        onClick = {
                            viewModel.onLoginClick()
                            onNavigateToHome()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Iniciar Sesión",
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

// ── CORRECCIÓN DEL MODIFIER ────────────────────────────────
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}