package com.chanho.motion

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class SampleWorkerScreenLock(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(ScreenLockBroadCastReceiver(), intentFilter)
        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        return Result.success(outputData)
    }
}
