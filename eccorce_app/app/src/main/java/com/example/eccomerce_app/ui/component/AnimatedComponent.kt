package com.example.eccomerce_app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun OpacityAndHideComponent(
    content: @Composable () -> Unit,
    isHideComponent: Boolean =true
) {
    AnimatedVisibility(
        visible = !isHideComponent,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column() {
            content()
        }
    }

}