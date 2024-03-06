package com.chanho.a.demo_gyro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        PrefHelper.init(this)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getSystemService(NotificationManager::class.java).run {
                val forChannel = NotificationChannel(
                    Constants.FORE_CHANNEL_ID,
                    "Foreground",
                    NotificationManager.IMPORTANCE_HIGH
                )
                createNotificationChannel(forChannel)
            }
        }
    }
}