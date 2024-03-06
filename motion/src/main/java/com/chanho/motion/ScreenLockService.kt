package com.chanho.motion

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

const val SERVICE_ID3 = 3

class ScreenLockService : Service() {

    lateinit var receiver: ScreenLockBroadCastReceiver
    lateinit var notiBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        receiver = ScreenLockBroadCastReceiver()
        registerReceiver(receiver, intentFilter)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intent = Intent(this, MotionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        notiBuilder = NotificationCompat.Builder(this, Constants.FORE_CHANNEL_ID)
            .setContentTitle("forground")
            .setContentText("잠금화면체크")
            .setSmallIcon(com.chanho.common.R.drawable.ic_launcher_waplat)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(SERVICE_ID3, notiBuilder.build())
        } else {
            startForeground(SERVICE_ID3, notiBuilder.build(), foregroundServiceType)
        }
        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        Log.e("screenLock_destroy", "락스크린 브로드캐스트 리시버 제거됨")
        callAlarmManager()
    }

    companion object {
        val LOCK_SCREEN_ON = "lock_screen_on"
    }

    private fun callAlarmManager() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.SECOND, 3)
        val intent = Intent(this, RestartAlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time, null), sender)
    }

}


class RestartLockScreenReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("restart screen lock service", "재시작")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(p0, ScreenLockService::class.java)
            p0?.startForegroundService(intent)
        } else {
            val intent = Intent(p0, ScreenLockService::class.java)
            p0?.startService(intent)
        }
    }

}

