package com.chanho.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chanho.common.data.AlarmDao
import com.chanho.common.databinding.ActivityAlarmPopupBinding
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AlarmPopupActivity @Inject constructor(
    private val alarmDao: AlarmDao
) : AppCompatActivity() {

    var isAlarmFirst: Boolean = true
        private set
    var content: String = ""
        private set
    var alarmTime: String? = ""
        private set
    var alarmCode: Int = -1
        private set
    private lateinit var binding: ActivityAlarmPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 안드12 이상에서 잠금화면 위로 액티비티 띄우기 & 화면 켜기

        intent.extras?.let {
            setData(this, it)
            setAlarmTime(this)
        }
        binding = ActivityAlarmPopupBinding.inflate(layoutInflater)
    }

    fun setData(context: Context, bundle: Bundle) {
        isAlarmFirst = bundle.getBoolean(Constants.IS_ALARM_FIRST, true)
        content = bundle.getString(Constants.CONTENT).toString()
        alarmTime = bundle.getString(Constants.ALARM_TIME)
        alarmCode = bundle.getInt(Constants.ALARM_CODE, -1)
    }

    fun setAlarmTime(context: Context) {
        val cal = Calendar.getInstance()
        cal.time = Util.dateFormat.parse(alarmTime)
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

        binding.alarmTitleText.text = content
        binding.alarmTimeText.text = formatTime
        with(binding) {
            registrationBtn.setOnClickListener {
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

                    // 복약 알림인경우 복약알림 반복등록함
                    Log.d(
                        "AlarmPopupActivity",
                        "AlarmPopupActivity MEDICATION 클릭 "
                    )
                    if (alarmDao.loadByAlarmCode(alarmCode)!=null) {
                        AlarmFunctions.cancelAlarm(
                            this@AlarmPopupActivity,
                            Util.dateFormat.format(calendar.time),
                            alarmCode,
                        )
                        val nowCalendar = Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis()
                            set(Calendar.AM_PM, calendar.get(Calendar.AM_PM))
                            set(Calendar.HOUR, calendar.get(Calendar.HOUR))
                            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                            set(Calendar.SECOND, calendar.get(Calendar.SECOND))
                        }
                        AlarmFunctions.setAlarmManager(
                            this@AlarmPopupActivity,
                            Util.dateFormat.format(nowCalendar.time),
                            alarmCode,
                            content,
                            isAlarmFirst = true
                        )
                    } else {
                        AlarmFunctions.cancelAlarm(
                            this@AlarmPopupActivity,
                            Util.dateFormat.format(calendar.time),
                            alarmCode,
                        )
                    }
                }
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.let {
            setData(this, it)
            setAlarmTime(this)
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
