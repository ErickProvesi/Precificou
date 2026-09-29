package com.erickprovesi.precificou.ui

import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.erickprovesi.precificou.Cadastro

object CadastroComposeHost {

    @JvmStatic
    fun show(activity: Cadastro) {

        val composeView = ComposeView(activity).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                CadastroScreen(

                    onRegisterClick = { _, _, _, _ ->

                        Toast.makeText(
                            activity,
                            "Integração do cadastro em andamento",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onBackClick = {
                        activity.openLoginFromCompose()
                    },

                    onPickImageClick = {
                        activity.openImagePickerFromCompose()
                    }
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
    }
}