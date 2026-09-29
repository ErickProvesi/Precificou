package com.erickprovesi.precificou.ui

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.erickprovesi.precificou.Cadastro

class CadastroUiStateHandle internal constructor(
    private val loading: MutableState<Boolean>,
    private val error: MutableState<String?>,
    private val nameError: MutableState<Boolean>,
    private val emailError: MutableState<Boolean>,
    private val passwordError: MutableState<Boolean>,
    private val confirmError: MutableState<Boolean>,
    private val profileImage: MutableState<ImageBitmap?>
) {

    fun setLoading(value: Boolean) {
        loading.value = value
    }

    fun clearError() {
        error.value = null
        nameError.value = false
        emailError.value = false
        passwordError.value = false
        confirmError.value = false
    }

    fun showError(message: String, field: String?) {
        clearError()

        error.value = message

        when (field) {
            "name" -> nameError.value = true
            "email" -> emailError.value = true
            "password" -> passwordError.value = true
            "confirm" -> confirmError.value = true

            "all" -> {
                nameError.value = true
                emailError.value = true
                passwordError.value = true
                confirmError.value = true
            }
        }
    }

    fun setProfileImage(bitmap: Bitmap) {
        profileImage.value = bitmap.asImageBitmap()
    }
}

object CadastroComposeHost {

    @JvmStatic
    fun show(activity: Cadastro): CadastroUiStateHandle {

        val loading = mutableStateOf(false)
        val error = mutableStateOf<String?>(null)

        val nameError = mutableStateOf(false)
        val emailError = mutableStateOf(false)
        val passwordError = mutableStateOf(false)
        val confirmError = mutableStateOf(false)

        val profileImage = mutableStateOf<ImageBitmap?>(null)

        val state = CadastroUiStateHandle(
            loading,
            error,
            nameError,
            emailError,
            passwordError,
            confirmError,
            profileImage
        )

        val composeView = ComposeView(activity).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                CadastroScreen(
                    onRegisterClick = { name, email, password, confirmPassword ->

                        activity.registerFromCompose(
                            name,
                            email,
                            password,
                            confirmPassword
                        )
                    },

                    onBackClick = {
                        activity.openLoginFromCompose()
                    },

                    onPickImageClick = {
                        activity.openImagePickerFromCompose()
                    },

                    profileImage = profileImage.value,

                    isLoading = loading.value,

                    errorMessage = error.value,

                    nameError = nameError.value,
                    emailError = emailError.value,
                    passwordError = passwordError.value,
                    confirmPasswordError = confirmError.value,

                    onInputChange = {
                        state.clearError()
                    }
                )
            }
        }

        activity.setContentView(composeView)

        return state
    }
}