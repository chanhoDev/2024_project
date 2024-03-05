package com.chanho.motion

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.PrefHelper

class SampleWorkerGyroScope(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        var inputData = inputData
        var number = inputData.getInt("number", -1)

        Log.e("sampleWorker_dowork", "number = $number")
        val intent = Intent(context, GyroScopeMotionService::class.java)
        try {
            Log.e("GyroScopeMotionService", "start")
            Thread.sleep(1000)

        } catch (e: InterruptedException) {
            e.printStackTrace()
            Result.failure()
            Log.e("sampleWorker exception1", "${e.printStackTrace()}")

        }
//        }
        Log.e("sampleWorker", "${PrefHelper[GyroScopeMotionService.GYROSCOPE, setOf<String>()]}")

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        context.startForegroundService(intent)
        return Result.success(outputData)
    }
}

