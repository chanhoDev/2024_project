package com.chanho.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.chanho.common.Constants.ALARM_CODE
import com.chanho.common.Constants.ALARM_TIME
import com.chanho.common.Constants.CONTENT
import com.chanho.common.Constants.IS_ALARM_FIRST
import com.chanho.common.data.AlarmDao
import com.chanho.common.data.AlarmDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver(): BroadcastReceiver() {

    @Inject lateinit var alarmDao: AlarmDao

    var content: String = ""
        private set
    var newAlarmTime: String? = ""
        private set
    var legacyAlarmTime: String? = ""
        private set
    var alarmTime: String? = ""
        private set

    var isContinueAlarmReceiver: Boolean = false
        private set

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG", "onReceive 알림이 들어옴 ${intent.action.toString()}")
        isContinueAlarmReceiver = false
        val isAlarmFirst = intent.getBooleanExtra(IS_ALARM_FIRST, true)
        content = intent.getStringExtra(CONTENT).toString()
        alarmTime = intent.getStringExtra(ALARM_TIME)
        val alarmCode = intent.getIntExtra(ALARM_CODE, 0)

        Log.d(
            "TAG",
            "intent.getStringExtra(CONTENT).isNullOrEmpty() =${
                intent.getStringExtra(CONTENT).isNullOrEmpty()
            }"
        )
        Log.d("TAG", "content =${content}")
        Log.d("TAG", "onReceive 알림이 들어옴 ${intent.action.toString()} time = ${alarmTime}")

        val cal = Calendar.getInstance()

        try {
            cal.time = Util.dateFormat.parse(alarmTime)
        } catch (e: ParseException) {
            e.printStackTrace()
            return
        }

        val ampm = if (cal.get(Calendar.AM_PM) == 0) {
            context.getString(R.string.common_am)
        } else {
            context.getString(R.string.common_pm)
        }
        var hour = cal.get(Calendar.HOUR)
        if (hour == 0) {
            hour = 12
        }
        val minute = cal.get(Calendar.MINUTE)
        val formatTime = context.getString(R.string.common_time_with_ampm, ampm, hour, minute)

        var datetime = Date()
        try {
            datetime = Util.dateFormat.parse(alarmTime) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
            return
        }

        isContinueAlarmReceiver = true

        val calendar = Calendar.getInstance().apply {
            this.time = datetime
        }

        AlarmFunctions.cancelAlarm(
            context,
            Util.dateFormat.format(calendar.time),
            alarmCode
        )

        val nowCalendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.AM_PM, calendar.get(Calendar.AM_PM))
            set(Calendar.HOUR, calendar.get(Calendar.HOUR))
            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, calendar.get(Calendar.SECOND))
        }
        if (alarmDao.loadByAlarmCode(alarmCode)!=null) {
            if (isAlarmFirst) {
                AlarmFunctions.setAlarmManager(
                    context,
                    Util.dateFormat.format(nowCalendar.time),
                    alarmCode,
                    content,
                    isAlarmFirst = false
                )
            } else {
                AlarmFunctions.setAlarmManager(
                    context,
                    Util.dateFormat.format(nowCalendar.time),
                    alarmCode,
                    content,
                    isAlarmFirst = true
                )
            }
            setNotification(
                context,
                title = content,
                body = formatTime,
                alarmTime = alarmTime ?: "",
                isAlarmFirst = isAlarmFirst,
                alarmCode = alarmCode
            )
        }
    }

    private fun migrateLegacyTimeToNewAlarmTime(
        legacyAlarmTime: String?,
        newAlarmTime: String?
    ): String {
        return legacyAlarmTime?.let { legacyTime ->
            legacyTime
        } ?: run {
            newAlarmTime ?: ""
        }
    }

    private fun setNotification(
        context: Context,
        title: String,
        body: String,
        alarmTime: String,
        isAlarmFirst: Boolean,
        alarmCode: Int
    ) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        notificationManager.cancel(alarmCode)

        val bundle = Bundle().apply {
            putString(CONTENT, title)
            putString(ALARM_TIME, alarmTime)
            putBoolean(IS_ALARM_FIRST, isAlarmFirst)
            putInt(ALARM_CODE, alarmCode)
        }

        val fullScreenIntent =
            Intent(context.applicationContext, AlarmPopupActivity::class.java).apply {
                putExtras(bundle)
            }

        val receiverIntent = Intent(context, AlarmNotificationActivity::class.java).apply {
            putExtras(bundle)
        }
        Log.d(
            "setNotification",
            "alarmTime = ${alarmTime} receiverIntent = ${receiverIntent.extras.toString()}"
        )

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context.applicationContext,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            alarmCode,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        CommonNotification.createNotificationForAlarmChannel(
            notificationManager
        )
        CommonNotification.deliverNotificationForAlarm(
            context,
            alarmCode,
            title,
            body,
            contentPendingIntent = contentPendingIntent,
            fullScreenPendingIntent = fullScreenPendingIntent,
        )
    }
}