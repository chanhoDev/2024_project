package com.chanho.motion

import android.Manifest
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.PendingIntent
import android.app.usage.EventStats
import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import com.chanho.motion.GyroScopeMotionService.Companion.GYROSCOPE
import com.chanho.motion.UserActivityBroadCastReceiver.Companion.DETECT
import com.chanho.motion.databinding.ActivityMotionBinding
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import java.lang.Exception
import java.util.Calendar
import java.util.Date
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
//                    val usageStateList = getAppUsageStats()
//                    showAppUsageStats(usageStateList)
//                    setPeriodTimeWorkerAppUsage(this@MotionActivity)
                    getForegroundActivity(this@MotionActivity)
                }
            }
        }
    }

    private fun getForegroundActivity(context: Context) {
        var packageName = "";
        var className = "";
        var cal_begin = Calendar.getInstance();
        cal_begin.set(Calendar.MINUTE, -10);
        var _begTime = cal_begin.getTimeInMillis()
        var _endTime = System.currentTimeMillis()
        var usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        PrefHelper["${untilCal.time} ~ ${currentCal.time}_${SampleWorkerAppUsage.APP_USAGE}"] = setString
        if (usageStatsManager != null) {
            var queryEvents = usageStatsManager.queryEvents(_begTime, _endTime)

            if (queryEvents != null) {
                var event = UsageEvents.Event()

                while (queryEvents.hasNextEvent()) {
                    var eventAux = UsageEvents.Event()
                    queryEvents.getNextEvent(eventAux)
//                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.ACTIVITY_PAUSED , packageName = $packageName event = ${event.shortcutId}")
//                    }
                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.ACTIVITY_RESUMED , packageName = $packageName event = ${event.shortcutId}")
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_STOPPED) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.ACTIVITY_STOPPED , packageName = $packageName event = ${event.shortcutId}")
                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.CONFIGURATION_CHANGE) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.CONFIGURATION_CHANGE , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.DEVICE_SHUTDOWN) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.DEVICE_SHUTDOWN , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.DEVICE_STARTUP) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.DEVICE_STARTUP , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.FOREGROUND_SERVICE_START) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.FOREGROUND_SERVICE_START , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.FOREGROUND_SERVICE_STOP) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.FOREGROUND_SERVICE_STOP , packageName = $packageName event = ${event.shortcutId}")
//                    }
                    if (eventAux.getEventType() == UsageEvents.Event.KEYGUARD_HIDDEN) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.KEYGUARD_HIDDEN , packageName = $packageName event = ${event.shortcutId}")
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.KEYGUARD_SHOWN) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.KEYGUARD_SHOWN , packageName = $packageName event = ${event.shortcutId}")
                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.NONE) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.NONE , packageName = $packageName event = ${event.shortcutId}")
//                    }
                    if (eventAux.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.SCREEN_INTERACTIVE , packageName = $packageName event = ${event.shortcutId}")
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.SCREEN_NON_INTERACTIVE , packageName = $packageName event = ${event.shortcutId}")
                    }

//                    if (eventAux.getEventType() == UsageEvents.Event.SHORTCUT_INVOCATION) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.SHORTCUT_INVOCATION , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.STANDBY_BUCKET_CHANGED) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.STANDBY_BUCKET_CHANGED , packageName = $packageName event = ${event.shortcutId}")
//                    }
//                    if (eventAux.getEventType() == UsageEvents.Event.USER_INTERACTION) {
//                        event = eventAux
//                        packageName = event.getPackageName()
////                        className = event.getClassName()
//                        var date = Date(event.timeStamp)
//                        val cal = Calendar.getInstance()
//                        cal.time = date
//                        var resultTime = Util.dateFormat.format(cal.time)
//                        Log.e("포그라운드활동","resultTime = $resultTime UsageEvents.Event.USER_INTERACTION , packageName = $packageName event = ${event.shortcutId}")
//                    }

                }

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

    private fun showAppUsageStats(usageStats: MutableList<UsageStats>) {
        usageStats.sortWith(Comparator { right, left ->
            compareValues(left.lastTimeUsed, right.lastTimeUsed)
        })
        val currentCal = Calendar.getInstance()

        val untilCal = Calendar.getInstance()
        untilCal.add(Calendar.HOUR, -2)

        Log.e("시간 범위 ", "${untilCal.time} ~ ${currentCal.time}")

        usageStats.forEach { it ->
            if (it.totalTimeInForeground > 0) {
                val lastTimeUsedDate = Date(it.lastTimeUsed)
                val lastTimeUsed = Util.dateFormat.format(lastTimeUsedDate)
                if (lastTimeUsedDate > untilCal.time) {
                    Log.d(
                        "showAppUsageState",
                        "packageName: ${it.packageName}, lastTimeUsed: ${lastTimeUsed}, " +
                                " totalTimeInForeground: ${it.totalTimeInForeground}"
                    )
                }
            }
        }
    }

    private fun getAppUsageStats(): MutableList<UsageStats> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)    // 1

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis() // 3
        )
        return queryUsageStats
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
        val state =
            WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC_GYROSCOPE").get()
        for (i in state) {
            Log.e("test5", "startWorkmanager:$state")
        }
    }

    private fun setPeriodTimeWorkerAppUsage(context: Context) {
        val data = Data.Builder()
            .putInt("number", 1)
            .build()

        val request =
            PeriodicWorkRequest.Builder(SampleWorkerAppUsage::class.java, 15, TimeUnit.MINUTES)
                .setInputData(data)
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

class SampleWorkerAppUsage(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        val APP_USAGE = "appUsage"
    }

    override fun doWork(): Result {
        val usageStateList = getAppUsageStats(context)
        showAppUsageStats(usageStateList)

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        return Result.success(outputData)
    }

    private fun showAppUsageStats(usageStats: MutableList<UsageStats>) {
        usageStats.sortWith(Comparator { right, left ->
            compareValues(left.lastTimeUsed, right.lastTimeUsed)
        })
        val currentCal = Calendar.getInstance()

        val untilCal = Calendar.getInstance()
        untilCal.add(Calendar.MINUTE, -15)

        Log.e("시간 범위 ", "${untilCal.time} ~ ${currentCal.time}")
        var setString = setOf<String>()
        usageStats.forEach { it ->
            if (it.totalTimeInForeground > 0) {
                val lastTimeUsedDate = Date(it.lastTimeUsed)
                val lastTimeUsed = Util.dateFormat.format(lastTimeUsedDate)
                if (lastTimeUsedDate > untilCal.time) {
                    setString = setString.plus(
                        "packageName: ${it.packageName}, lastTimeUsed: ${lastTimeUsed}, " +
                                " totalTimeInForeground: ${it.totalTimeInForeground}"
                    )
                    Log.d(
                        "showAppUsageState",
                        "packageName: ${it.packageName}, lastTimeUsed: ${lastTimeUsed}, " +
                                " totalTimeInForeground: ${it.totalTimeInForeground}"
                    )
                }
            }
        }
        PrefHelper["${untilCal.time} ~ ${currentCal.time}_${APP_USAGE}"] = setString
    }

    private fun getAppUsageStats(context: Context): MutableList<UsageStats> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)    // 1

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis() // 3
        )
        return queryUsageStats
    }

    private fun getQueryEvent(context: Context): UsageEvents {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)    // 1

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        //28 부터 사용 가능
        val queryEvents =
            usageStatsManager.queryEvents(cal.timeInMillis, System.currentTimeMillis())
        return queryEvents
    }


}