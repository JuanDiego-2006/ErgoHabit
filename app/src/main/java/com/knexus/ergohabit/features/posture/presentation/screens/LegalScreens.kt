package com.knexus.ergohabit.features.posture.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.GreenPrimary

@Composable
fun LegalSection(number: String, title: String, content: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D5A4C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D5A4C),
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun AvisoPrivacidadScreen(onAccept: () -> Unit, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(top = 80.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Indicador de arrastre
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.LightGray)
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "DOCUMENTO LEGAL", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "Aviso de Privacidad", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D5A4C))
                Text(text = "ErgoHabit · Última actualización: Julio 2026", fontSize = 12.sp, color = Color.LightGray)
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    LegalSection(
                        number = "1",
                        title = "Naturaleza del Proyecto",
                        content = "• Ergohabit es una aplicación sin fines comerciales.\n\n" +
                                "• Toda la relación del usuario con la aplicación se rige mediante acuerdos claros de uso académico y escolar de investigación.\n\n" +
                                "• Toda la información recabada se orienta estrictamente al autoaprendizaje del alumno y al análisis estadístico escolar sin lucros.\n\n" +
                                "• La aplicación no realiza rastreo externo ni venta de datos."
                    )
                    LegalSection(
                        number = "2",
                        title = "Datos Recopilados y Minimización (Norma ISO 29100)",
                        content = "• La aplicación recopila Información de Identificación Personal (PII), la cual incluye correo electrónico, foto de perfil y métricas de comportamiento diario.\n\n" +
                                "• Siguiendo el principio de minimización de datos, ErgoHabit solo procesa los datos cervicales estrictamente necesarios para el análisis de postura y la mínima información de usuario.\n\n" +
                                "• Para el cálculo de la postura, la aplicación requiere mantener encendida la cámara del dispositivo para su análisis en tiempo real sin embargo, no se captura, almacena ni guarda ninguna fotografía o imagen durante este proceso.\n\n" +
                                "• La aplicación no accede en ningún momento a la geolocalización ni a los contactos del usuario."
                    )
                    LegalSection(
                        number = "3",
                        title = "Seguridad y Protección de Datos (Normas ISO 27001 e ISO 27701)",
                        content = "• La información recopilada es tratada con la máxima confidencialidad técnica para proteger la Información de Identificación Personal (PII).\n\n" +
                                "• El backend de la aplicación cuenta con una arquitectura segura definida por la Norma ISO/IEC 27001 la cual garantiza la confidencialidad, integridad y disponibilidad de las métricas físicas, hábitos y horarios de los usuarios.\n\n" +
                                "• El tratamiento ético de los datos se controla mediante el establecimiento de un Sistema de Gestión de Información de Privacidad (PIMS), conforme a la Norma ISO 27701.\n\n" +
                                "• Una vez concluido el periodo de prueba de 25 días del proyecto, toda la información recabada será eliminada de manera definitiva."
                    )
                    LegalSection(
                        number = "4",
                        title = "Consentimiento y Elección",
                        content = "• La aplicación implementa un diálogo de consentimiento explícito al inicio de su ejecución.\n\n" +
                                "• El usuario tiene derecho a elegir su nivel de participación, pudiendo retirar su consentimiento o pausar el rastreo de información en cualquier momento."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5A4C)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("He leído y acepto", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cerrar", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun TerminosCondicionesScreen(onAccept: () -> Unit, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(top = 80.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Indicador de arrastre
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.LightGray)
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(text = "DOCUMENTO LEGAL", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "Términos y Condiciones", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D5A4C))
                Text(text = "ErgoHabit · Última actualización: Julio 2026", fontSize = 12.sp, color = Color.LightGray)
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    LegalSection(
                        number = "1",
                        title = "Información General",
                        content = "• La aplicación móvil \"Ergohabit\" es un Proyecto Integrador de carácter estrictamente académico y de investigación.\n\n" +
                                "• El equipo desarrollador se deslinda de cualquier responsabilidad derivada del uso de la aplicación.\n\n" +
                                "• El objetivo principal de la aplicación es la gestión de hábitos saludables y el uso correcto de los dispositivos móviles."
                    )
                    LegalSection(
                        number = "2",
                        title = "Requisitos y Elegibilidad del Usuario",
                        content = "• El uso de la aplicación está restringido a usuarios que cuenten con un dispositivo móvil con sistema operativo Android.\n\n" +
                                "• Se excluyen dispositivos Android con versiones de software obsoletas, así como usuarios de sistemas operativos diferentes, como iOS.\n\n" +
                                "• La aplicación está diseñada para una población con un rango de edad de entre 16 y 50 años.\n\n" +
                                "• Las personas menores de 16 años o mayores de 50 años están excluidas del uso de la plataforma.\n\n" +
                                "• Para utilizar la aplicación, el usuario debe presentar un uso cotidiano y prolongado del dispositivo móvil.\n\n" +
                                "• Es un requisito indispensable que el usuario otorgue su consentimiento explícito para interactuar con la aplicación y evaluar sus funciones."
                    )
                    LegalSection(
                        number = "3",
                        title = "Funciones y Estándares de Calidad",
                        content = "• Ergohabit proporciona alarmas de postura, registro de hábitos y notificaciones de pausas activas.\n\n" +
                                "• Estas funciones están respaldadas por la Norma ISO 10075 para mitigar el agotamiento, prevenir la fatiga, la monotonía y el estrés mental tras periodos prolongados de trabajo continuo.\n\n" +
                                "• El software opera bajo la Norma ISO 25010, la cual garantiza que las funciones descritas operen estrictamente según los requisitos planteados para el bienestar del usuario.\n\n" +
                                "• La interfaz está diseñada bajo las normativas ISO 9241-11 e ISO 9241-210, enfocándose en la usabilidad, eficiencia de flujos, prevención de la fatiga cognitiva y el diseño centrado en el usuario."
                    )
                    LegalSection(
                        number = "4",
                        title = "Control del Usuario",
                        content = "• El usuario mantiene el control sobre su experiencia y tiene el poder de pausar el rastreo de sus hábitos en cualquier momento."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5A4C)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("He leído y acepto", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cerrar", color = Color.Gray)
                }
            }
        }
    }
}
