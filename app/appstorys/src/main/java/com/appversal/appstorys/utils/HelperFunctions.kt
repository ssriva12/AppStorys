package com.appversal.appstorys.utils

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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



fun Context.pxToDp(px: Float): Dp {
    return (px / resources.displayMetrics.density).dp
}