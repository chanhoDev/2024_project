package com.chanho.motion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    var broadCastedCalled = false
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BootReceiver onReceive", "$intent ${intent.action.toString()}")
        //android.intent.action.BOOT_COMPLETED 으로 테스트 해야하지만 앱 테슽트할때에는 불가하여 intent.action.BOOT_COMPLETED 로 테스트
        broadCastedCalled = when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                val intent = Intent(context, MotionService::class.java)
                ContextCompat.startForegroundService(context, intent)
                true
            }
            else -> {
                false
            }
        }
    }
}