package com.example.carousal.utils.config

object ApiConfig {
    private const val BASE_URL = "https://backend.appstorys.com/api/v1/"
    
    object Endpoints {
        const val TRACK_SCREEN = "${BASE_URL}users/track-screen/"
        const val TRACK_ACTION = "${BASE_URL}users/track-action/"
        const val TRACK_USER = "${BASE_URL}users/track-user/"
        const val VALIDATE_ACCOUNT = "${BASE_URL}admins/validate-account/"
        const val UPDATE_USER = "${BASE_URL}users/update-user/"
    }
} 