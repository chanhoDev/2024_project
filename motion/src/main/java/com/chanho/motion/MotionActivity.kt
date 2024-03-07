package com.chanho.motion

import android.Manifest
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.chanho.common.Util
import com.chanho.motion.SampleWorkerAppUsage.Companion.PREV_INITIAL_DATE
import com.chanho.motion.databinding.ActivityMotionBinding
import java.util.Calendar
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
            motionServiceBtnA.setOnClickListener {
                val intent = Intent(this@MotionActivity, MotionServiceA::class.java)
                startForegroundService(intent)
            }
            motionServiceBtnB.setOnClickListener {
                val intent = Intent(this@MotionActivity, MotionServiceB::class.java)
                startForegroundService(intent)
            }
            lockScreenBtn.setOnClickListener {
                //잠금화면 활성화 클릭 이벤트 처리1
//                setPeriodTimeWorkerScreenLock(this@MotionActivity)
                val intent = Intent(this@MotionActivity, ScreenLockService::class.java)
                startForegroundService(intent)
            }

            usageStateManagerBtn.setOnClickListener {
                if (!checkForPermission()) {
                    Log.i(
                        this@MotionActivity.packageName,
                        "The user may not allow the access to apps usage. "
                    )
                    Toast.makeText(
                        this@MotionActivity,
                        "Failed to retrieve app usage statistics. " +
                                "You may need to enable access for this app through " +
                                "Settings > Security > Apps with usage access",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                } else {
                    setPeriodTimeWorkerAppUsage(this@MotionActivity)
                }
            }
            periodPushAlarmCheckBtn.setOnClickListener {
                //1분주기로 푸시알림을 발생한다
                val cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.MINUTE,1)
                val intent = Intent(this@MotionActivity,PeriodPushBroadCastReceiver::class.java)
                val sender = PendingIntent.getBroadcast(this@MotionActivity,0,intent, PendingIntent.FLAG_IMMUTABLE)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time,null),sender)
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

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }

    private fun setPeriodTimeWorkerGyroscope(context: Context) {
        val data = Data.Builder()
            .putInt("number", 1)
            .build()

        val request =
            PeriodicWorkRequest.Builder(SampleWorkerGyroScope::class.java, 15, TimeUnit.HOURS)
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
        val state =
            WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC_GYROSCOPE").get()
        for (i in state) {
            Log.e("test5", "startWorkmanager:$state")
        }
    }

    private fun setPeriodTimeWorkerAppUsage(context: Context) {
//        val cal = Calendar.getInstance()
//        cal.set(Calendar.MINUTE,-15)
//        val prevInitialDate = Util.dateFormat.format(cal.time)
//        val data = Data.Builder()
//            .putString(PREV_INITIAL_DATE, prevInitialDate)
//            .build()

        val request =
            PeriodicWorkRequest.Builder(SampleWorkerAppUsage::class.java, 15, TimeUnit.MINUTES)
//                .setInputData(data)
//                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .addTag("PERIODIC_APP_USAGE")
                .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "PERIODIC_APP_USAGE",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        //워크매니저 상태 확인
        val state =
            WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC_APP_USAGE").get()
        for (i in state) {
            Log.e("app_usage_manager", "startWorkmanager:$state")
        }
    }

    private fun setPeriodTimeWorkerTransition(context: Context) {
        val data = Data.Builder()
            .putInt("number", 1)
            .build()

        val workerRequest =
            PeriodicWorkRequest.Builder(SampleWorkerTransition::class.java, 30, TimeUnit.MINUTES)
                .setInputData(data)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .addTag("PERIODIC_TRANSITION")
                .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "PERIODIC_TRANSITION",
            ExistingPeriodicWorkPolicy.KEEP,
            workerRequest
        )
        //워크매니저 상태 확인
        val state =
            WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC_TRANSITION").get()
        for (i in state) {
            Log.e("test5", "startWorkmanager:$state")
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




