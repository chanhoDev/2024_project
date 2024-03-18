package com.chanho.motion

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask


class MotionServiceChangeTime : Service(), SensorEventListener {
    private var shakeTime = 0L

    //    private var shakeCount = 0
    lateinit var notiBuilder: NotificationCompat.Builder
    private lateinit var sensorManager: SensorManager
    private lateinit var acceleroMeter: Sensor
    private lateinit var acceleroMeterGravity: Sensor
    private var checkUserMotion = false
    private var cal = Calendar.getInstance()


    companion object {
        val CHECK_5_MIN_CODE = 1000
        val SHAKE_THRESHOLD_GRAVITY = 1.05F
        val SHAKE_SKIP_TIME = 500
        val REST_TIME = "restTime"
        val MAX_REST_TIME = 120
        val MIN_REST_TIME = 5
    }

    override fun onCreate() {
        super.onCreate()
        checkUserMotion = false
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acceleroMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
        acceleroMeterGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) as Sensor
        check5Min()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.registerListener(this, acceleroMeter, SensorManager.SENSOR_DELAY_NORMAL)

        val intent = Intent(this, MotionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        notiBuilder = NotificationCompat.Builder(this, Constants.FORE_CHANNEL_ID)
            .setContentTitle("forgroundChangeTime")
            .setOnlyAlertOnce(true)
            .setSmallIcon(com.chanho.common.R.drawable.ic_launcher_waplat)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(SERVICE_ID, notiBuilder.build())
        } else {
            startForeground(SERVICE_ID, notiBuilder.build(), foregroundServiceType)
        }

        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        cal = Calendar.getInstance()
        Log.e("motionService", "onDestroy!! shakeTime = ${Util.dateFormat.format(cal.time)}")
        sensorManager.unregisterListener(this)
        PrefHelper[Util.dateFormat.format(cal.time)] = "${Util.dateFormat.format(cal.time)}"
        setRestTime()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                var axisX = it.values[0]
                var axisY = it.values[1]
                var axisZ = it.values[2]

                var gravityX = axisX / SensorManager.GRAVITY_EARTH
                var gravityY = axisY / SensorManager.GRAVITY_EARTH
                var gravityZ = axisZ / SensorManager.GRAVITY_EARTH

                var resultF = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ
                var squardD = Math.sqrt(resultF.toDouble())
                var fForce = squardD.toFloat()
                if (fForce > SHAKE_THRESHOLD_GRAVITY) {
                    val currentTime = System.currentTimeMillis()
                    if (shakeTime + SHAKE_SKIP_TIME > currentTime) {
                        return@let
                    }
                    GlobalScope.launch {
                        cal = Calendar.getInstance()
                        onUserMotionChanged()
                        shakeTime = currentTime
                        val manager = getSystemService(NotificationManager::class.java)
                        notiBuilder.setContentText("진행상황:${Util.dateFormat.format(cal.time)}")
                        manager.notify(SERVICE_ID, notiBuilder.build())
                        manager.cancel(SERVICE_ID)
                    }
                    Log.e("결과값", "onSensorChanged:Shake 발생 시간 ${Util.dateFormat.format(cal.time)}")
                }
            }
        }
    }

    private fun onUserMotionChanged() {
        checkUserMotion = true
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =  Intent(this, TerminateSensorBroadCastReceiverChageTime::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            CHECK_5_MIN_CODE,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_NO_CREATE
        )
        try {
            alarmManager.cancel(pendingIntent)
        }catch (e:Exception){
        }
        stopService(Intent(this, MotionServiceChangeTime::class.java))
    }

    private fun check5Min() {
        //5분이 끝나면 서비스를 종료한다
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 5)
        val intent = Intent(this, TerminateSensorBroadCastReceiverChageTime::class.java)
        val sender =
            PendingIntent.getBroadcast(this, CHECK_5_MIN_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.time.time, null), sender)
    }

    private fun setRestTime() {
        var restTime = PrefHelper[REST_TIME, MIN_REST_TIME]
        if (checkUserMotion) {
            if (restTime < MAX_REST_TIME) {
                restTime += MIN_REST_TIME
            }
        } else {
            restTime = MIN_REST_TIME
        }
        PrefHelper[REST_TIME] = restTime

        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.MINUTE, restTime)
        val intent = Intent(this, RestartAlarmReceiverChangeTime::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time, null), sender)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

class RestartAlarmReceiverChangeTime : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(p0, MotionServiceChangeTime::class.java)
            p0?.startForegroundService(intent)
        } else {
            val intent = Intent(p0, MotionServiceChangeTime::class.java)
            p0?.startService(intent)
        }
        Toast.makeText(p0, "restart service", Toast.LENGTH_SHORT).show()
    }
}

class TerminateSensorBroadCastReceiverChageTime() : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("TerminateSensorBroadCastReceiverC", "TerminateSensorBroadCastReceiverChageTime")
        p0?.stopService(Intent(p0, MotionServiceChangeTime::class.java))
    }

}
