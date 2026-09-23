package com.example.projeto2.ui

import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.projeto2.Login
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

        activity.addContentView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        return BiConsumer { message, shouldHighlight ->
            loginError.value = message
            highlightFields.value = shouldHighlight
        }
    }
}
