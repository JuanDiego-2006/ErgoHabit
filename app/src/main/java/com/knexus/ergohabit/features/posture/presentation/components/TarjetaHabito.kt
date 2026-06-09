package com.knexus.ergohabit.features.posture.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.ui.theme.*

@Composable
fun HabitCard(emoji: String, name: String, meta: String, pct: Int, pctColor: Color, progressColor: Color, completed: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(14.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(GreenLight)) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text(text = "$pct%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = pctColor)
                }
                LinearProgressIndicator(
                    progress = { pct / 100f },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(7.dp).clip(RoundedCornerShape(6.dp)),
                    color = progressColor, trackColor = Color(0xFFE8EDE9)
                )
                Text(text = meta, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
            }
            if (completed) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(30.dp).clip(CircleShape).background(PurpleAccent)) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = BgWhite, modifier = Modifier.size(18.dp))
                }
            } else {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}