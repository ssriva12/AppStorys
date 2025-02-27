package com.appversal.appstorys.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color

fun isGifUrl(url: String): Boolean {
    return url.lowercase().endsWith(".gif")
}

fun String?.toColor(defaultColor: Color): Color {
    return try {
        if (this.isNullOrEmpty()){
            defaultColor
        }else{
            Color(android.graphics.Color.parseColor(this))
        }
    }catch(_: Exception){
        defaultColor
    }
}

fun String.removeDoubleQuotes(): String = this.replace("\"", "")

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
