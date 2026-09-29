package com.erickprovesi.precificou.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrecificouYellow = Color(0xFFFFE500)
private val DarkBackground = Color(0xFF101010)
private val DarkCard = Color(0xFF1E1E1E)
private val LightBackground = Color(0xFFF6F6F3)

@Composable
fun CadastroScreen(
    onRegisterClick: (String, String, String, String) -> Unit,
    onBackClick: () -> Unit = {},
    onPickImageClick: () -> Unit = {},
    profileImage: ImageBitmap? = null,
    darkTheme: Boolean? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    nameError: Boolean = false,
    emailError: Boolean = false,
    passwordError: Boolean = false,
    confirmPasswordError: Boolean = false,
    onInputChange: () -> Unit = {}
) {

    val isDark = darkTheme ?: isSystemInDarkTheme()

    val backgroundColor =
        if (isDark) DarkBackground else LightBackground

    val cardColor =
        if (isDark) DarkCard else Color.White

    val titleColor =
        if (isDark) Color.White else Color(0xFF111111)

    val subtitleColor =
        if (isDark) Color(0xFFD7D7D7) else Color(0xFF555555)

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

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var showContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        showContent = true
    }

    MaterialTheme(colorScheme = colors) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = 12.dp, top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = if (isDark) Color.White else Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 24.dp,
                        vertical = 72.dp
                    ),
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
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(112.dp)
                                    .clickable(
                                        onClick = onPickImageClick
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Surface(
                                    modifier = Modifier.size(104.dp),
                                    shape = CircleShape,
                                    color = if (isDark) {
                                        Color(0xFF2A2A2A)
                                    } else {
                                        Color(0xFFF0F0F0)
                                    }
                                ) {

                                    if (profileImage != null) {

                                        Image(
                                            bitmap = profileImage,
                                            contentDescription = "Foto de perfil",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )

                                    } else {

                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(52.dp),
                                                tint = if (isDark) {
                                                    Color(0xFFAAAAAA)
                                                } else {
                                                    Color(0xFF777777)
                                                }
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .align(Alignment.BottomEnd),
                                    shape = CircleShape,
                                    color = PrecificouYellow
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AddAPhoto,
                                            contentDescription = "Selecionar foto",
                                            modifier = Modifier.size(18.dp),
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(18.dp)
                            )

                            Text(
                                text = "Crie sua conta",
                                color = titleColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = "Comece a dar valor ao seu trabalho.",
                                color = subtitleColor,
                                fontSize = 15.sp
                            )

                            Spacer(
                                modifier = Modifier.height(26.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    onInputChange()
                                },
                                label = {
                                    Text("Nome")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words
                                ),
                                shape = RoundedCornerShape(16.dp),
                                isError = nameError
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    onInputChange()
                                },
                                label = {
                                    Text("E-mail")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                ),
                                shape = RoundedCornerShape(16.dp),
                                isError = emailError
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    onInputChange()
                                },
                                label = {
                                    Text("Senha")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation =
                                    if (passwordVisible) {
                                        VisualTransformation.None
                                    } else {
                                        PasswordVisualTransformation()
                                    },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                trailingIcon = {

                                    IconButton(
                                        onClick = {
                                            passwordVisible =
                                                !passwordVisible
                                        }
                                    ) {

                                        Icon(
                                            imageVector =
                                                if (passwordVisible) {
                                                    Icons.Filled.Visibility
                                                } else {
                                                    Icons.Filled.VisibilityOff
                                                },
                                            contentDescription =
                                                if (passwordVisible) {
                                                    "Ocultar senha"
                                                } else {
                                                    "Mostrar senha"
                                                },
                                            tint = if (isDark) {
                                                PrecificouYellow
                                            } else {
                                                Color(0xFF8A7200)
                                            }
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                isError = passwordError
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    onInputChange()
                                },
                                label = {
                                    Text("Confirmar senha")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation =
                                    if (confirmPasswordVisible) {
                                        VisualTransformation.None
                                    } else {
                                        PasswordVisualTransformation()
                                    },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                trailingIcon = {

                                    IconButton(
                                        onClick = {
                                            confirmPasswordVisible =
                                                !confirmPasswordVisible
                                        }
                                    ) {

                                        Icon(
                                            imageVector =
                                                if (confirmPasswordVisible) {
                                                    Icons.Filled.Visibility
                                                } else {
                                                    Icons.Filled.VisibilityOff
                                                },
                                            contentDescription =
                                                if (confirmPasswordVisible) {
                                                    "Ocultar senha"
                                                } else {
                                                    "Mostrar senha"
                                                },
                                            tint = if (isDark) {
                                                PrecificouYellow
                                            } else {
                                                Color(0xFF8A7200)
                                            }
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                isError = confirmPasswordError
                            )

                            if (errorMessage != null) {

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(24.dp)
                            )

                            Button(
                                onClick = {
                                    onRegisterClick(
                                        name.trim(),
                                        email.trim(),
                                        password,
                                        confirmPassword
                                    )
                                },
                                enabled =
                                    name.isNotBlank() &&
                                            email.isNotBlank() &&
                                            password.isNotBlank() &&
                                            confirmPassword.isNotBlank() &&
                                            !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrecificouYellow,
                                    contentColor = Color.Black,
                                    disabledContainerColor =
                                        if (isLoading) {
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
                                        text = "Criar conta",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Já possui uma conta?",
                                    color = subtitleColor,
                                    fontSize = 14.sp
                                )

                                androidx.compose.material3.TextButton(
                                    onClick = onBackClick
                                ) {
                                    Text(
                                        text = "Entrar",
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
}

@Preview(
    name = "Cadastro - Tema escuro",
    showBackground = true
)
@Composable
private fun CadastroDarkPreview() {
    CadastroScreen(
        onRegisterClick = { _, _, _, _ -> },
        darkTheme = true
    )
}

@Preview(
    name = "Cadastro - Tema claro",
    showBackground = true
)
@Composable
private fun CadastroLightPreview() {
    CadastroScreen(
        onRegisterClick = { _, _, _, _ -> },
        darkTheme = false
    )
}