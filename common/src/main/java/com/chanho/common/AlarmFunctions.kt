package com.chanho.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.chanho.common.Constants.ALARM_CODE
import com.chanho.common.Constants.ALARM_TIME
import com.chanho.common.Constants.ALARM_TYPE
import com.chanho.common.Constants.CONTENT
import com.chanho.common.Constants.IS_ALARM_FIRST
import com.chanho.common.Util.IS_DEVELOPER_MODE
import com.chanho.common.Util.dateFormat
import java.text.ParseException
import java.util.Calendar
import java.util.Date

object AlarmFunctions {


    fun callAlarm(
        context: Context,
        time: String,
        alarmCode: Int,
        content: String,
        isAlarmFirst: Boolean = true
    ) {

        val isAlarmRegistered = isAlarmRegistered(context,alarmCode)
        if(isAlarmRegistered){
            Log.d("alarm Already exist ", "$isAlarmRegistered")
        }else{
            Log.d("alarm not exist", "$isAlarmRegistered")
                setAlarmManager(
                    context,
                    time,
                    alarmCode,
                    content,
                    isAlarmFirst = isAlarmFirst
                )
        }
    }
    fun isAlarmRegistered(context: Context, alarmCode: Int): Boolean {
        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmCode,
            receiverIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_NO_CREATE else PendingIntent.FLAG_NO_CREATE
        )
        return pendingIntent != null
    }

    fun setAlarmManager(
        context: Context,
        time: String,
        alarmCode: Int,
        content: String,
        isAlarmFirst: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        Log.d("setAlarmManager", "setAlarmManager ALARM_TIME = ${time}")
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            Bundle().apply {
                putExtra(CONTENT, content)
                putExtra(ALARM_TIME, time)
                putExtra(IS_ALARM_FIRST, isAlarmFirst)
                putExtra(ALARM_CODE, alarmCode)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmCode,
            receiverIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )
        var datetime = Date()
        try {
            datetime = dateFormat.parse(time) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val resultDate = setAlarmTime(
            datetime,
            isAlarmFirst,
        )
        if (resultDate != null) {
            try {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    resultDate.timeInMillis,
                    pendingIntent
                )
                Log.d("resultAlarmManager", "알림 등록 성공 ")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("resultAlarmManager", "알림 등록 실패 ")

            }
        }
    }

    fun setAlarmTime(
        datetime: Date,
        isAlarmFirst: Boolean,
    ): Calendar? {
        val calendar = Calendar.getInstance()
        calendar.time = datetime

            val nowCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.AM_PM, calendar.get(Calendar.AM_PM))
                set(Calendar.HOUR, calendar.get(Calendar.HOUR))
                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, calendar.get(Calendar.SECOND))
            }
            if (isAlarmFirst) {
                // 재알림 TEST용 1분뒤 등록
                Log.d(" test1", "처음 등록했을때 test1 ${nowCalendar.time}")

                if (nowCalendar.time < Date()) {
                    // 재알림 TEST용 1분뒤 등록
                    if (IS_DEVELOPER_MODE) {
                        nowCalendar.timeInMillis = System.currentTimeMillis()
                        nowCalendar.add(Calendar.MINUTE, 2)
                    } else {
                        nowCalendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    // 1일 뒤 등록
                    Log.d(
                        "test2",
                        " 확인함 다음날 반복등록 test2 처음 등록했으나 이미 과거인 경우  ${nowCalendar.time}"
                    )
                }
            } else {
                // 재알림 TEST용 1분뒤 등록
                if (IS_DEVELOPER_MODE) {
                    nowCalendar.timeInMillis = System.currentTimeMillis()
                    nowCalendar.add(Calendar.MINUTE, 1)
                } else {
                    // 5분뒤 등록
                    nowCalendar.add(Calendar.MINUTE, 5)
                }

                Log.d("test3", "확인 안함 재알림 test3  ${nowCalendar.time}")
            }
            return nowCalendar
    }

    fun cancelAlarm(
        context: Context,
        time: String,
        alarmCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmCode,
            receiverIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_NO_CREATE
        )
        try {
            Log.d("cancelAlarm", "알림 취소 alarmCode = $alarmCode time = $time  ")
            alarmManager?.cancel(pendingIntent)
        } catch (e: Exception) {
            Log.d("cancelAlarm", "알림 취소 에러 alarmCode = $alarmCode time = $time e = ${e.message}")
            e.printStackTrace()
        }
    }
}