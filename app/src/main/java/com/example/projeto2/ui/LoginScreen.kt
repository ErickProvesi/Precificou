package com.example.projeto2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projeto2.R

private val PrecificouYellow = Color(0xFFFFE500)
private val DarkBackground = Color(0xFF101010)
private val DarkCard = Color(0xFF1E1E1E)
private val LightBackground = Color(0xFFF6F6F3)

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onGoogleClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    darkTheme: Boolean? = null,
    errorMessage: String? = null,
    highlightFields: Boolean = false,
    onInputChange: () -> Unit = {},

) {
    val isDark = darkTheme ?: isSystemInDarkTheme()

    var isLoading by rememberSaveable { mutableStateOf(false) }

    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val cardColor = if (isDark) DarkCard else Color.White
    val titleColor = if (isDark) Color.White else Color(0xFF111111)
    val subtitleColor = if (isDark) Color(0xFFD7D7D7) else Color(0xFF555555)
    val borderColor = if (isDark) Color(0xFF3C3C3C) else Color(0xFFDADADA)
    val googleButtonBorder = if (isDark) Color(0xFF4A4A4A) else Color(0xFFD3D3D3)

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            isLoading = false
        }
    }

    val colors = if (isDark) {
        darkColorScheme(
            primary = PrecificouYellow,
            background = DarkBackground,
            surface = DarkCard
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFB59A00),
            background = LightBackground,
            surface = Color.White
        )
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    MaterialTheme(colorScheme = colors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(550)
                ) + slideInVertically(
                    initialOffsetY = { it / 8 },
                    animationSpec = tween(550)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 460.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Surface(
                            modifier = Modifier.size(104.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF2F2F2)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_precificou),
                                    contentDescription = "Logo Precificou",
                                    modifier = Modifier
                                        .size(78.dp)
                                        .clip(RoundedCornerShape(18.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Bem-vindo ao Precificou",
                            color = titleColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Seu trabalho tem valor.",
                            color = subtitleColor,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                onInputChange()
                            },
                            label = { Text("E-mail") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            shape = RoundedCornerShape(16.dp),
                            isError = highlightFields,

                        )

                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                onInputChange()
                            },
                            label = { Text("Senha") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Filled.Visibility
                                        else
                                            Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            "Ocultar senha"
                                        else
                                            "Mostrar senha",
                                        tint = if (isDark) PrecificouYellow else Color(0xFF8A7200)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            isError = highlightFields,

                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onForgotPasswordClick) {
                                Text("Esqueceu a senha?")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (!isLoading) {
                                    isLoading = true
                                    onLoginClick(email.trim(), password)
                                }
                            },
                            enabled = email.isNotBlank() &&
                                    password.isNotBlank() &&
                                    !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrecificouYellow,
                                contentColor = Color.Black,
                                disabledContainerColor = if (isLoading) {
                                    PrecificouYellow
                                } else if (isDark) {
                                    Color(0xFF383838)
                                } else {
                                    Color(0xFFEAEAEA)
                                },
                                disabledContentColor = Color.Gray
                            )
                        ) {

                            if (isLoading) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Text(
                                    text = "Entrar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(modifier = Modifier.weight(1f), color = borderColor)
                            Text(
                                text = "  ou  ",
                                color = subtitleColor,
                                fontSize = 13.sp
                            )
                            Divider(modifier = Modifier.weight(1f), color = borderColor)
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        OutlinedButton(
                            onClick = onGoogleClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                googleButtonBorder
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = titleColor
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Continuar com Google",
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ainda não tem conta?",
                                color = subtitleColor
                            )
                            TextButton(onClick = onRegisterClick) {
                                Text(
                                    text = "Cadastre-se",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

            }


        }
    }
}

@Preview(name = "Precificou - Tema escuro", showBackground = true)
@Composable
private fun LoginDarkPreview() {
    LoginScreen(
        onLoginClick = { _, _ -> },
        darkTheme = true
    )
}

@Preview(name = "Precificou - Tema claro", showBackground = true)
@Composable
private fun LoginLightPreview() {
    LoginScreen(
        onLoginClick = { _, _ -> },
        darkTheme = false
    )
}