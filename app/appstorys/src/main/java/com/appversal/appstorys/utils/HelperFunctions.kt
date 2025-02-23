package com.appversal.appstorys.utils

fun isGifUrl(url: String): Boolean {
    return url.lowercase().endsWith(".gif")
}
