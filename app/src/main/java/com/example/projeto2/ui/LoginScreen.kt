package com.example.projeto2.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

private val PrecificouYellow = Color(0xFFFFE500)

@Composable
fun LoginScreen() {

    val darkTheme = isSystemInDarkTheme()

    val backgroundColor = if (darkTheme) {
        Color(0xFF171717)
    } else {
        Color(0xFFF7F7F4)
    }

    val textColor = if (darkTheme) {
        Color.White
    } else {
        Color(0xFF171717)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Precificou!",
                color = PrecificouYellow,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Seu trabalho tem valor.",
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(
    name = "Login - Tema escuro",
    showBackground = true
)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}