package com.agrovision.ai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgroVisionApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
