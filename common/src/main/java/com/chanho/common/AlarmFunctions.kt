package com.chanho.common

import android.annotation.SuppressLint
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
import java.util.*

object AlarmFunctions {
    fun callAlarm(
        context: Context,
        time: String,
        alarmPopupType: Constants.AlarmPopupType,
        alarmCode: Int,
        content: String,
        isAlarmFirst: Boolean = true
    ) {
        setContentAlarmCode(
            alarmPopupType,
            content,
            alarmCode.toString()
        )
        if (getTime(alarmPopupType).filter { it == time }.isNullOrEmpty()) {
            Log.d("call Alarm", alarmCode.toString())
            setTime(
                alarmPopupType,
                getTime(alarmPopupType).toMutableSet().plus(time)
            )
            setAlarmManager(
                context,
                time,
                alarmPopupType,
                alarmCode,
                content,
                isAlarmFirst = isAlarmFirst
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun setAlarmManager(
        context: Context,
        time: String,
        alarmPopupType: Constants.AlarmPopupType,
        alarmCode: Int,
        content: String,
        isAlarmFirst: Boolean = true
    ) {
        setTimeAlarmCode(
            alarmPopupType,
            time,
            alarmCode.toString()
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        Log.d("setAlarmManager", "setAlarmManager ALARM_TIME = ${time}")
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            Bundle().apply {
                putExtra(CONTENT, content)
                putExtra(ALARM_TIME, time)
                putExtra(ALARM_TYPE, alarmPopupType.typeName)
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
            alarmPopupType,
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
        alarmPopupType: Constants.AlarmPopupType,
    ): Calendar? {
        val calendar = Calendar.getInstance()
        calendar.time = datetime

        if (Constants.AlarmPopupType.MEDICATION == alarmPopupType) {
            val nowCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.AM_PM, calendar.get(Calendar.AM_PM))
                set(Calendar.HOUR, calendar.get(Calendar.HOUR))
                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, calendar.get(Calendar.SECOND))
            }
            if (isAlarmFirst) {
                // 재알림 TEST용 1분뒤 등록
                Log.d("${alarmPopupType} test1", "처음 등록했을때 test1 ${nowCalendar.time}")

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
                        "${alarmPopupType} test2",
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

                Log.d("${alarmPopupType} test3", "확인 안함 재알림 test3  ${nowCalendar.time}")
            }
            return nowCalendar
        } else {
            if (!isAlarmFirst) {
                if (IS_DEVELOPER_MODE) {
                    // 재알림 TEST용 1분뒤 등록
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.add(Calendar.MINUTE, 1)
                } else {
                    // 5분뒤 등록
                    calendar.add(Calendar.MINUTE, 5)
                }
                Log.d("${alarmPopupType}test4", "확인 안함 재알림 test4 ${calendar.time}")
            }

            if (Date() < calendar.time) {
                return calendar
            }
            return null
        }
    }

    fun cancelAlarm(
        context: Context,
        alarmPopupType: Constants.AlarmPopupType,
        time: String,
        alarmCode: Int,
        isRepeat: Boolean? = false
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmCode,
            receiverIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_NO_CREATE
        )

        if (isRepeat == false) {
            deleteTime(alarmPopupType, time)
            removeContentAlarmCode(alarmPopupType, time)
            removeTimeAlarmCode(alarmPopupType, time)
        }
        try {
            Log.d("cancelAlarm", "알림 취소 alarmCode = $alarmCode time = $time  ")
            alarmManager?.cancel(pendingIntent)
        } catch (e: Exception) {
            Log.d("cancelAlarm", "알림 취소 에러 alarmCode = $alarmCode time = $time e = ${e.message}")
            e.printStackTrace()
        }
    }


    fun cancelAllAlarm(
        context: Context,
    ) {
        val medicationSet = getTime(Constants.AlarmPopupType.MEDICATION)
        val scheduleSet = getTime(Constants.AlarmPopupType.SCHEDULE)

        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
        try {
            medicationSet.forEach { time ->
                alarmManager?.cancel(
                    PendingIntent.getBroadcast(
                        context,
                        getTimeAlarmCode(Constants.AlarmPopupType.MEDICATION, time).toInt(),
                        receiverIntent,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_NO_CREATE
                    )
                )
            }
            scheduleSet.forEach { time ->
                alarmManager?.cancel(
                    PendingIntent.getBroadcast(
                        context,
                        getTimeAlarmCode(Constants.AlarmPopupType.SCHEDULE, time).toInt(),
                        receiverIntent,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_NO_CREATE
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getTime(Constants.AlarmPopupType.MEDICATION).forEach { time ->
            removeContentAlarmCode(
                Constants.AlarmPopupType.MEDICATION,
                time
            )
            removeTimeAlarmCode(
                Constants.AlarmPopupType.MEDICATION,
                time
            )
        }
        getTime(Constants.AlarmPopupType.SCHEDULE).forEach { time ->
            removeContentAlarmCode(
                Constants.AlarmPopupType.SCHEDULE,
                time
            )
            removeTimeAlarmCode(
                Constants.AlarmPopupType.SCHEDULE,
                time
            )
        }
        setTime(Constants.AlarmPopupType.MEDICATION, setOf())
        setTime(Constants.AlarmPopupType.SCHEDULE, setOf())

    }

    fun setContentAlarmCode(
        alarmPopupType: Constants.AlarmPopupType,
        content: String,
        alarmCode: String
    ) {
        PrefHelper["${alarmPopupType.typeName}_${alarmCode}"] = content
    }

    private fun removeContentAlarmCode(
        alarmPopupType: Constants.AlarmPopupType,
        alarmCode: String
    ) {
        PrefHelper.removeKey("${alarmPopupType.typeName}_${alarmCode}")
    }

    fun setTimeAlarmCode(
        alarmPopupType: Constants.AlarmPopupType,
        time: String,
        alarmCode: String
    ) {
        PrefHelper["${alarmPopupType.typeName}_${time}"] = alarmCode
    }

    fun getTimeAlarmCode(alarmPopupType: Constants.AlarmPopupType, time: String): String {
        return PrefHelper["${alarmPopupType.typeName}_${time}"]
    }

    private fun removeTimeAlarmCode(alarmPopupType: Constants.AlarmPopupType, time: String) {
        PrefHelper.removeKey("${alarmPopupType.typeName}_${time}")
    }

    private fun setTime(alarmPopupType: Constants.AlarmPopupType, time: Set<String>) {
        PrefHelper[alarmPopupType.typeName] = time
    }

    private fun getTime(alarmPopupType: Constants.AlarmPopupType): Set<String> {
        return PrefHelper[alarmPopupType.typeName, setOf()]
    }

    private fun deleteTime(alarmPopupType: Constants.AlarmPopupType, time: String) {
        val timeSet = getTime(alarmPopupType).toMutableSet()
        timeSet.remove(time)
        setTime(alarmPopupType, timeSet)

    }

}