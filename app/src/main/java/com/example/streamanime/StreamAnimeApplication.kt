package com.example.streamanime

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StreamAnimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}