package com.chanho.motion

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.PrefHelper
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity


class SampleWorkerTransition(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        var inputData = inputData
        var number = inputData.getInt("number", -1)

        Log.e("sampleWorker", "${PrefHelper[UserActivityBroadCastReceiver.DETECT, setOf<String>()]}")

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        val transitions = mutableListOf<ActivityTransition>()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        val request = ActivityTransitionRequest(transitions)
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val userActivityBroadCastReceiver =
            Intent(context, UserActivityBroadCastReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            userActivityBroadCastReceiver,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            val task = ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent)

            task.addOnSuccessListener {
                // Handle success
                Log.e("ACTIVITY_RECOGNITION", "ACTIVITY_RECOGNITION 성공 ${it.toString()}")
            }

            task.addOnFailureListener { e: Exception ->
                // Handle error
                Log.e("ACTIVITY_RECOGNITION", "ACTIVITY_RECOGNITION 실패 ${e.toString()}")

            }
        }
        return Result.success(outputData)
    }

}