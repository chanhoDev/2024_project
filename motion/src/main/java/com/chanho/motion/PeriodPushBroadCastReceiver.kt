package com.chanho.motion

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.graphics.drawable.IconCompat
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.R
import com.chanho.common.Util
import java.util.Calendar


//정해진 주기에 따른 alarmManager의 신호에 따라 notification 을 받는 receiver
class PeriodPushBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.MINUTE,1)
        val intent = Intent(context,PeriodPushBroadCastReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time,null),sender)

        Log.e("주기적인 푸시알림", "SOS 알림!")
        val currentCal = Calendar.getInstance()
        currentCal.timeInMillis = System.currentTimeMillis()



        val contentPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, PeriodPushConfirmCheckReceiver::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.color.green_00A88A),
            context.getString(R.string.confirm),
            contentPendingIntent
        ).build()

        var manager = context.getSystemService(NotificationManager::class.java)
        var notiBuilder = NotificationCompat.Builder(context, Constants.FORE_CHANNEL_ID)
            .setContentTitle("SOS 알림!")
            .setContentText("알림시간: ${Util.dateFormat.format(currentCal.time)}")
            .setSmallIcon(com.chanho.common.R.drawable.ic_launcher_waplat)
            .addAction(action)

        manager.notify(SERVICE_ID3, notiBuilder.build())
    }
}

// notification 알림을 클릭했을때 기능 동작하는 receiver
class PeriodPushConfirmCheckReceiver() : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("PeriodPushConfirmCheckReceiver", "onReceive 알림이 들어옴")
        Toast.makeText(p0,"PeriodPushConfirmCheckReceiver",Toast.LENGTH_SHORT).show()
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        PrefHelper["${Util.dateFormat.format(cal.time)}_PERIOD_PUSH_CONFIRM"] = "${Util.dateFormat.format(cal.time)}"
    }

}