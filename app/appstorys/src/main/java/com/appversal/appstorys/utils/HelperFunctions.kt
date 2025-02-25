package com.appversal.appstorys.utils

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