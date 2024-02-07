package com.chanho.demo

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}