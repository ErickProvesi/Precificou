package com.erickprovesi.precificou.ui
import android.widget.Toast

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.erickprovesi.precificou.Login
import androidx.compose.runtime.mutableStateOf
import java.util.function.BiConsumer

object LoginComposeHost {

    @JvmStatic
    fun show(activity: Login): BiConsumer<String, Boolean> {

        val loginError = mutableStateOf<String?>(null)
        val highlightFields = mutableStateOf(false)

        val notConnected = {
            Toast.makeText(
                activity,
                "Funcionalidade em integração",
                Toast.LENGTH_SHORT
            ).show()
        }

        val composeView = ComposeView(activity).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                LoginScreen(
                    onLoginClick = { email, password ->
                        activity.authenticateFromCompose(email, password)
                    },
                    onGoogleClick = {
                        activity.openGoogleFromCompose()
                    },

                    onRegisterClick = {
                        activity.openRegistrationFromCompose()
                    },

                    onForgotPasswordClick = {
                        activity.openForgotPasswordFromCompose()
                    },
                    errorMessage = loginError.value,
                    highlightFields = highlightFields.value,
                    onInputChange = {
                        loginError.value = null
                        highlightFields.value = false
                    },

                )
            }
        }

        activity.setContentView(composeView)
        return BiConsumer { message, shouldHighlight ->
            loginError.value = message
            highlightFields.value = shouldHighlight
        }
    }
}
