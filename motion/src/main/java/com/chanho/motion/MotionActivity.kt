package com.chanho.motion

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.PrefHelper
import com.chanho.motion.GyroScopeMotionService.Companion.GYROSCOPE
import com.chanho.motion.UserActivityBroadCastReceiver.Companion.DETECT
import com.chanho.motion.databinding.ActivityMotionBinding
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMotionBinding

    enum class UIStatus {
        INITIAL,    //앱 진입 시 초기 상태
        AGAIN,      //거부했지만, 다시 요청해야 하는 상태
        DIRECT      //2번 또는 사용자가 직접 시스템에서 거부한 상태라, 요청할 수 없는 상태
    }

    private var uiStatus = UIStatus.INITIAL
    private val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            arrayOf("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBindView()
        onObserve(this)

    }

    private fun onBindView() {
        with(binding) {
            gyroscopeWorkerBtn.setOnClickListener {
                setPeriodTimeWorkerGyroscope(this@MotionActivity)
            }
            transitionWorkerBtn.setOnClickListener {
                if (uiStatus == UIStatus.DIRECT) {
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    ).also {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        this@MotionActivity,
                        permissions,
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
            gyroscopeServiceStartBtn.setOnClickListener {
                val intent = Intent(this@MotionActivity, GyroScopeMotionService::class.java)
                startForegroundService(intent)
            }
            motionServiceBtn.setOnClickListener {
                val intent = Intent(this@MotionActivity, MotionService::class.java)
                startForegroundService(intent)
            }
        }
    }

    private fun onObserve(context: Context) {
        WorkManager.getInstance(context).getWorkInfosByTagLiveData("download").observe(this) {
            it.forEach {
                if (it.state.isFinished) {
                    Log.e("observe_download", it.toString())
                }
            }
        }
        WorkManager.getInstance(context).getWorkInfosByTagLiveData("Periodic").observe(this) {
            it.forEach {
                if (it.state.isFinished) {
                    Log.e("observe_Periodic", it.toString())
                } else {

                }
            }
        }
    }


    private fun setPeriodTimeWorkerGyroscope(context: Context) {
        val data = Data.Builder()
            .putInt("number", 1)
            .build()

        val request =
            PeriodicWorkRequest.Builder(SampleWorkerGyroScope::class.java, 15, TimeUnit.MINUTES)
                .setInputData(data)
//                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .addTag("Periodic_GYROSCOPE")
                .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "PERIODIC_GYROSCOPE",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        //워크매니저 상태 확인
        val state = WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC_GYROSCOPE").get()
        for (i in state) {
            Log.e("test5", "startWorkmanager:$state")
        }
    }

    private fun setPeriodTimeWorkerTransition(context: Context) {
//        val data = Data.Builder()
//            .putInt("number", 1)
//            .build()
//
//        val request =
//            PeriodicWorkRequest.Builder(SampleWorkerTransition::class.java, 30, TimeUnit.MINUTES)
//                .setInputData(data)
//                .setInitialDelay(2, TimeUnit.SECONDS)
//                .addTag("PERIODIC_TRANSITION")
//                .build()
//
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "PERIODIC_TRANSITION",
//            ExistingPeriodicWorkPolicy.KEEP,
//            request
//        )
//        //워크매니저 상태 확인
//        val state = WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC").get()
//        for (i in state) {
//            Log.e("test5", "startWorkmanager:$state")
//        }
        val transitions = mutableListOf<ActivityTransition>()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        val request = ActivityTransitionRequest(transitions)
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val userActivityBroadCastReceiver = Intent(context, UserActivityBroadCastReceiver::class.java)

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
                Log.e("ACTIVITY_RECOGNITION","ACTIVITY_RECOGNITION 성공 ${it.toString()}")
            }

            task.addOnFailureListener { e: Exception ->
                // Handle error
                Log.e("ACTIVITY_RECOGNITION","ACTIVITY_RECOGNITION 실패 ${e.toString()}")

            }
        }
    }

    private fun cancelWorkManager(context: Context, cancelRequestId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(cancelRequestId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                val requiredGrantResults =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        grantResults
                    } else {
                        grantResults
                    }

                if (requiredGrantResults.isNotEmpty() && requiredGrantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    setPeriodTimeWorkerTransition(this@MotionActivity)
                }
            }
        }
    }

    companion object {
        val REQUEST_CODE_PERMISSIONS = 1000
    }
}


class SampleWorkerTransition(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        var inputData = inputData
        var number = inputData.getInt("number", -1)

        Log.e("sampleWorker", "${PrefHelper[DETECT, setOf<String>()]}")

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        val transitions = mutableListOf<ActivityTransition>()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        val request = ActivityTransitionRequest(transitions)
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val userActivityBroadCastReceiver = Intent(context, UserActivityBroadCastReceiver::class.java)

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
                Log.e("ACTIVITY_RECOGNITION","ACTIVITY_RECOGNITION 성공 ${it.toString()}")
            }

            task.addOnFailureListener { e: Exception ->
                // Handle error
                Log.e("ACTIVITY_RECOGNITION","ACTIVITY_RECOGNITION 실패 ${e.toString()}")

            }
        }
        return Result.success(outputData)
    }

}


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
        Log.e("sampleWorker", "${PrefHelper[GYROSCOPE, setOf<String>()]}")

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        context.startForegroundService(intent)
        return Result.success(outputData)
    }

}