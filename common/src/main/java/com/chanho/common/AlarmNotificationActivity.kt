package com.chanho.common

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.text.ParseException
import java.util.Calendar
import java.util.Date

class AlarmNotificationActivity : AppCompatActivity() {
    //다른앱 띄우기 권한이 없는 경우 복약, 일정의 푸시알림을 클릭했을때 알림 관련 처리하고 화면을 띄워주기 위함 화면
    var content = ""
        private set
    var alarmTime = ""
        private set
    var alarmType = ""
        private set
    var alarmCode = -1
        private set

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_notification)
        Log.e("AlarmNotificationActivity", "AlarmNotificationActivity onReceive ${intent.extras}")
        if (setAlarmNotification(this, intent.extras)) {
            cancelAlarmNotification(alarmCode)
            val alarmIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("waplat://com.nhn.waplat/product_service")
            }
            startActivity(alarmIntent)
        }
        finish()
    }

    private fun cancelAlarmNotification(alarmCode:Int){
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmCode)
    }


    fun setAlarmNotification(context: Context, bundle: Bundle?): Boolean {
        bundle?.let {
            bundle.getBoolean(Constants.IS_ALARM_FIRST).let { isAlarmFirst ->
                alarmTime = bundle.getString(Constants.ALARM_TIME) ?: ""
                alarmType = bundle.getString(Constants.ALARM_TYPE) ?: ""
                content = if (bundle.getString(Constants.CONTENT).isNullOrEmpty()) {
                    if (alarmType == Constants.AlarmPopupType.MEDICATION.typeName) {
                        context.getString(R.string.medication_alarm_title)
                    } else {
                        ""
                    }
                } else {
                    bundle.getString(Constants.CONTENT).toString()
                }
                alarmCode = bundle.getInt(Constants.ALARM_CODE, -1)
                Log.d(
                    "AlarmNotificationActivity",
                    "isAlarmFirst = ${isAlarmFirst} alarmType = ${alarmType} alarmTime = ${alarmTime}"
                )

                if (isAlarmFirst) {
                    var datetime = Date()
                    try {
                        datetime = Util.dateFormat.parse(alarmTime) as Date
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    val calendar = Calendar.getInstance().apply {
                        this.time = datetime
                    }
                    //TODO 복약 알림인경우 복약알림 반복등록함
                    Log.d("AlarmNotificationActivity", "alarmType = ${alarmType} ")
                    when (Constants.AlarmPopupType.getAlarmPopupType(alarmType)) {
                        Constants.AlarmPopupType.MEDICATION -> {
                            Log.d(
                                "AlarmNotificationActivity",
                                "AlarmNotificationActivity MEDICATION 클릭 "
                            )
                            if (AlarmFunctions.getTimeAlarmCode(
                                    Constants.AlarmPopupType.getAlarmPopupType(
                                        alarmType
                                    ), alarmTime
                                ).isNotEmpty()
                            ) {
                                AlarmFunctions.cancelAlarm(
                                    this,
                                    Constants.AlarmPopupType.getAlarmPopupType(alarmType),
                                    Util.dateFormat.format(calendar.time),
                                    alarmCode,
                                    isRepeat = true
                                )
                                val nowCalendar = Calendar.getInstance().apply {
                                    timeInMillis = System.currentTimeMillis()
                                    set(Calendar.AM_PM, calendar.get(Calendar.AM_PM))
                                    set(Calendar.HOUR, calendar.get(Calendar.HOUR))
                                    set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                                    set(Calendar.SECOND, calendar.get(Calendar.SECOND))
                                }
                                AlarmFunctions.setAlarmManager(
                                    this,
                                    Util.dateFormat.format(nowCalendar.time),
                                    Constants.AlarmPopupType.getAlarmPopupType(alarmType),
                                    alarmCode,
                                    content,
                                    isAlarmFirst = true
                                )
                            } else {
                                AlarmFunctions.cancelAlarm(
                                    this,
                                    Constants.AlarmPopupType.getAlarmPopupType(alarmType),
                                    Util.dateFormat.format(calendar.time),
                                    alarmCode,
                                    isRepeat = false
                                )
                            }
                        }

                        Constants.AlarmPopupType.SCHEDULE -> {
                            Log.d(
                                "AlarmNotificationActivity",
                                "AlarmNotificationActivity SCHEDULE 클릭 "
                            )
                            AlarmFunctions.cancelAlarm(
                                this,
                                Constants.AlarmPopupType.getAlarmPopupType(alarmType),
                                Util.dateFormat.format(calendar.time),
                                alarmCode,
                                isRepeat = false
                            )
                        }
                    }
                }
                return true
            }
        } ?: run {
            return false
        }
    }
}