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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask


class MotionServiceC : Service(), SensorEventListener {
    private var shakeTime = 0L
    private var shakeCount = 0
    lateinit var notiBuilder: NotificationCompat.Builder
    private lateinit var sensorManager: SensorManager
    private lateinit var acceleroMeter: Sensor
    private lateinit var acceleroMeterGravity: Sensor


    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acceleroMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
        acceleroMeterGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) as Sensor
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.MINUTE,5)
        val intent = Intent(this,TerminateSensorBroadCastReceiverC::class.java)
        val sender = PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time,null),sender)
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
            .setContentTitle("forgroundC")
            .setContentText("진행상황:$shakeCount")
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
        Log.e("motionService", "onDestroy!! shakeCount = $shakeCount")
        sensorManager.unregisterListener(this)
        PrefHelper[SHAKE_COUNT_C] = shakeCount
//        val intent = Intent(this, MotionService::class.java)
//        ContextCompat.startForegroundService(this, intent)
        callAlarmManager()
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
                    shakeTime = currentTime
                    shakeCount++
                    GlobalScope.launch {
                        val manager = getSystemService(NotificationManager::class.java)
                        notiBuilder.setContentText("진행상황:$shakeCount")
                        manager.notify(SERVICE_ID, notiBuilder.build())
                        manager.cancel(SERVICE_ID)
                    }
                    Log.e("결과값", "onSensorChanged:Shake 발생 $shakeTime $shakeCount")
                }
            }
        }
    }

    private fun callAlarmManager(){
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.MINUTE,30)
        val intent = Intent(this,RestartAlarmReceiverC::class.java)
        val sender = PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time,null),sender)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    companion object {
        val SHAKE_THRESHOLD_GRAVITY = 1.05F
        val SHAKE_SKIP_TIME = 500
        val SHAKE_COUNT_C = "shakeCount_c"
    }
}

class RestartAlarmReceiverC : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(p0, MotionServiceC::class.java)
            p0?.startForegroundService(intent)
        } else {
            val intent = Intent(p0, MotionServiceC::class.java)
            p0?.startService(intent)
        }
        Toast.makeText(p0, "restart service", Toast.LENGTH_SHORT).show()
    }
}
class TerminateSensorBroadCastReceiverC():BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("TerminateSensorBroadCastReceiverC","TerminateSensorBroadCastReceiverC")
        p0?.stopService(Intent(p0,MotionServiceC::class.java))
    }

}