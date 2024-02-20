package com.chanho.motion

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import com.chanho.motion.GyroScopeMotionService.Companion.GYROSCOPE
import com.chanho.motion.MotionService.Companion.SHAKE_COUNT
import com.chanho.motion.databinding.ActivityMotionBinding
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMotionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBindView()
        onObserve(this)

    }

    private fun onBindView() {
        with(binding) {
            downloadWorkerBtn.setOnClickListener {
//                val intent = Intent(this@MotionActivity, MotionService::class.java)
//                ContextCompat.startForegroundService(this@MotionActivity, intent)
                setOneTimeWorker(this@MotionActivity)
            }
            periodWorkerBtn.setOnClickListener {
//                val intent = Intent(this@MotionActivity, MotionService::class.java)
//                ContextCompat.startForegroundService(this@MotionActivity, intent)
//                stopService(intent)
                setPeriodTimeWorker(this@MotionActivity)
            }
            downloadWorkerStopBtn.setOnClickListener {
                //자이로 스코프 동작 관련 확인
//                val intent = Intent(this@MotionActivity, MotionService::class.java)
//                stopService(intent)
//                cancelWorkManager(this@MotionActivity, "download")
//                var intent = Intent(this@MotionActivity, GyroScopeMotionService::class.java)
//                ContextCompat.startForegroundService(this@MotionActivity, intent)
            }
            periodWorkerStopBtn.setOnClickListener {
//                val intent = Intent(this@MotionActivity, MotionService::class.java)
//                stopService(intent)
//                cancelWorkManager(this@MotionActivity, "Periodic")
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

    private fun setOneTimeWorker(context: Context) {
        val data = Data.Builder()
            .putInt("number", 10)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val downloadRequest = OneTimeWorkRequest.Builder(SampleWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
//            .setInitialDelay(, TimeUnit.SECONDS)
            .addTag("download")
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)
    }

    private fun setPeriodTimeWorker(context: Context) {
        val data = Data.Builder()
            .putInt("number", 1)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val downloadRequest =
            PeriodicWorkRequest.Builder(SampleWorker::class.java, 15, TimeUnit.MINUTES)
                .setInputData(data)
//                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .addTag("Periodic")
                .build()


//        WorkManager.getInstance(context).enqueue(downloadRequest)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "PERIODIC",
            ExistingPeriodicWorkPolicy.KEEP,
            downloadRequest
        )
        //워크매니저 상태 확인
        val state = WorkManager.getInstance(context).getWorkInfosForUniqueWork("PERIODIC").get()
        for (i in state) {
            Log.e("test5", "startWorkmanager:$state")
        }
    }

    private fun cancelWorkManager(context: Context, cancelRequestId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(cancelRequestId)
    }
}


class SampleWorker(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor

    //roll and pitch
    var timeStamp: Double = 0.0
    var dt: Double = 0.0
    val NS2S = 1.0f / 1000000000.0f
    var RAD2DGR = 180 / Math.PI
    var pitch: Double = 0.0 // y 축 방향의 각도
    var roll: Double = 0.0 //x축
    var yaw: Double = 0.0// z축
    override fun doWork(): Result {
        var inputData = inputData
        var number = inputData.getInt("number", -1)

        Log.e("sampleWorker_dowork", "number = $number")
//        for (i in 1..number) {
//        Log.e("for", "dowork = $i")\
        val intent = Intent(context, GyroScopeMotionService::class.java)
        try {

            Log.e("GyroScopeMotionService", "start")
//            Thread.sleep(1000)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                stopForeground(servi);
//            }
//            context.startForegroundService(intent)


        } catch (e: InterruptedException) {
            e.printStackTrace()
            Result.failure()
        }
//        }
        Log.e("sampleWorker", "${PrefHelper[GYROSCOPE, setOf<String>()]}")

        val outputData = Data.Builder()
            .putInt("number", 15)
            .build()
        sensorManager = context.getSystemService(Service.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) as Sensor
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI)
//            context.stopService(intent)

        Handler(Looper.getMainLooper()).postDelayed({
            var setString: Set<String> = PrefHelper[GYROSCOPE, setOf<String>()]
            val cal = Calendar.getInstance()
            val time = Util.dateFormat.format(cal.time)
            val resultPitch = String.format("%.1f", pitch * RAD2DGR)
            val resultRoll = String.format("%.1f", roll * RAD2DGR)
            val resultYaw = String.format("%.1f", yaw * RAD2DGR)
            setString =
                setString.plus("[time=$time,pitch=$resultPitch//roll=$resultRoll//yaw=$resultYaw]")
            PrefHelper[GYROSCOPE] = setString
            sensorManager.unregisterListener(this)
            Log.e("GyroScopeMotionService", "finish ${PrefHelper[GYROSCOPE, setOf<String>()]}")
        }, 3000)
        return Result.success(outputData)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let { event ->
            //각 축의 각속도 성분을 받는다
            var gyroX = event.values[0]
            var gyroY = event.values[1]
            var gyroZ = event.values[2]
            //각속도를 적분하여 회전각을 추출하기 위해 적분 간격dt를 구한다.
            //dt : 센서가 현재 상태를 감지하는 시간 간격
            //NS2S: Nano second -> second
            dt = (event.timestamp - timeStamp) * NS2S
            timeStamp = event.timestamp.toDouble()

            //맨 센서 인식을 활성화하여 처음 timeStamp가 0일때는 dt 값이 올바르지 않으므로 넘어간다
            if (dt - timeStamp * NS2S != 0.0) {
                //각속도 성분을 적분 -> 회전각(pitch,roll)으로 변환
                //여기까지의 pitch, roll의 단위는 라디안이다
                //그래서 아래 로그 출력부분에서 멤버변수 RAD2DGR를 곱해주어 degree로 변환해줌
                pitch += gyroY * dt
                roll += gyroX * dt
                yaw += gyroZ * dt

                Log.e(
                    "LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                            + "           [Y]:" + String.format("%.4f", event.values[1])
                            + "           [Z]:" + String.format("%.4f", event.values[2])
                            + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                            + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                            + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                            + "           [dt]: " + String.format("%.4f", dt)
                )
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


}