package com.knexus.ergohabit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knexus.ergohabit.R
import com.knexus.ergohabit.ui.theme.BrandMint
import com.knexus.ergohabit.ui.theme.TextPrimary

@Composable
fun ErgoHabitBrandHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val iconSize = if (compact) 88.dp else 104.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = "ErgoHabit",
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(if (compact) 12.dp else 16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold)) {
                    append("ergo")
                }
                withStyle(SpanStyle(color = BrandMint, fontWeight = FontWeight.Bold)) {
                    append("habit")
                }
            },
            fontSize = if (compact) 26.sp else 30.sp
        )
    }
}
