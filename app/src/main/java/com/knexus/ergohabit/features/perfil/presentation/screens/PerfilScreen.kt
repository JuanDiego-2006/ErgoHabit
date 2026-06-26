package com.knexus.ergohabit.features.perfil.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.knexus.ergohabit.features.posture.presentation.components.BarraNavegacionInferior
import com.knexus.ergohabit.features.perfil.presentation.viewmodel.PerfilViewModel
import com.knexus.ergohabit.core.navigation.NavRuta
import com.knexus.ergohabit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavHostController,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.actualizarFoto(it.toString()) }
    }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = BgMain,
        bottomBar = { BarraNavegacionInferior(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.usuario == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Foto de Perfil Circular (Igual a la imagen)
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E4D3B)), // Verde oscuro
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.usuario?.fotoUrl != null) {
                                AsyncImage(
                                    model = uiState.usuario?.fotoUrl,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp),
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Icono de cámara circular azul
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF67B7BD), // Azul turquesa
                            modifier = Modifier
                                .size(40.dp)
                                .border(4.dp, BgMain, CircleShape)
                                .clickable { launcher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre en grande (ailyn)
                Text(
                    text = uiState.usuario?.nombre ?: "Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E4D3B),
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Card de Información Personal (Blanca y Redondeada)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Información Personal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            
                            // Botón Editar estilo burbuja azul
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFFE0F7F9),
                                modifier = Modifier.clickable { viewModel.toggleEdit(!uiState.isEditing) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isEditing) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFF67B7BD),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (uiState.isEditing) "Cancelar" else "Editar",
                                        color = Color(0xFF67B7BD),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (!uiState.isEditing) {
                            InfoItem(Icons.Default.PersonOutline, "NOMBRE(S)", uiState.usuario?.nombre ?: "")
                            InfoItem(Icons.Default.PersonOutline, "PRIMER APELLIDO", uiState.usuario?.primerApellido ?: "")
                            InfoItem(Icons.Default.PersonOutline, "SEGUNDO APELLIDO", uiState.usuario?.segundoApellido ?: "")
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                            
                            InfoItem(Icons.Default.Email, "CORREO ELECTRÓNICO", uiState.usuario?.correo ?: "", isEmail = true)
                        } else {
                            EditField(Icons.Default.Person, "NOMBRE(S)", uiState.editNombre, viewModel::onNombreChange)
                            EditField(Icons.Default.Person, "PRIMER APELLIDO", uiState.editPrimerApellido, viewModel::onPrimerApellidoChange)
                            EditField(Icons.Default.Person, "SEGUNDO APELLIDO", uiState.editSegundoApellido, viewModel::onSegundoApellidoChange)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { viewModel.guardarCambios() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Cerrar sesión rojo brillante
                Button(
                    onClick = {
                        viewModel.cerrarSesion {
                            navController.navigate(NavRuta.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(65.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cerrar sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, isEmail: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF90A4AE),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF90A4AE),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                style = if (isEmail) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = if (isEmail) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun EditField(icon: ImageVector, label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}
