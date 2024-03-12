package com.chanho.motion

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import com.google.gson.Gson
import java.util.Calendar
import java.util.Date


//정해진 주기에 따른 alarmManager의 신호에 따라 notification 을 받는 receiver
class AppUsageBroadCastReceiver : BroadcastReceiver() {
    companion object {
        const val PREV_INITIAL_DATE = "prevInitialDate"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prevCal = Calendar.getInstance()
        prevCal.set(Calendar.MINUTE, -5)
        var prevInitialDate = PrefHelper[PREV_INITIAL_DATE, Util.dateFormat.format(prevCal.time)]
        val currentCal = Calendar.getInstance()
        val currentDate = Util.dateFormat.format(currentCal.time)

        getForegroundActivity(context, prevInitialDate, currentDate)

        prevInitialDate = Util.dateFormat.format(currentCal.time)
        PrefHelper[PREV_INITIAL_DATE] = prevInitialDate
    }

    private fun getForegroundActivity(
        context: Context,
        prevInitialDate: String,
        currentDate: String
    ) {
        var usageEventList = ArrayList<UsageEvent>()
        var packageName = ""
        var cal_start = Calendar.getInstance()
        cal_start.time = Util.dateFormat.parse(prevInitialDate)
        var _begTime = cal_start.timeInMillis
        val cal_end = Calendar.getInstance()
        cal_end.time = Util.dateFormat.parse(currentDate)

        var _endTime = cal_end.timeInMillis
        var usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        if (usageStatsManager != null) {
            var queryEvents = usageStatsManager.queryEvents(_begTime, _endTime)

            if (queryEvents != null) {
                var event = UsageEvents.Event()
                while (queryEvents.hasNextEvent()) {
                    var eventAux = UsageEvents.Event()
                    queryEvents.getNextEvent(eventAux)

                    if (eventAux.getEventType() == UsageEvents.Event.KEYGUARD_HIDDEN) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "KEYGUARD_HIDDEN"
                            )
                        )
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.KEYGUARD_SHOWN) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "KEYGUARD_SHOWN"
                            )
                        )
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "SCREEN_INTERACTIVE"
                            )
                        )
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "SCREEN_NON_INTERACTIVE"
                            )
                        )
                    }

                    if (eventAux.getEventType() == UsageEvents.Event.USER_INTERACTION) {
                        event = eventAux
                        packageName = event.getPackageName()
//                        className = event.getClassName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "USER_INTERACTION"
                            )
                        )
                    }
                }
                if(usageEventList.map { it.eventType }.contains("KEYGUARD")){
                    //5분 후에 동작
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = System.currentTimeMillis()
                    cal.add(Calendar.MINUTE,5)
                    val intent = Intent(context,SensorServiceAndUsageBroadCastReceiver::class.java)
                    val sender = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_IMMUTABLE)
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(cal.time.time,null),sender)
                }else{
                    val intent = Intent(context, SensorService::class.java)
                    context.startForegroundService(intent)

                }

            }
        }
    }


    private fun saveEventData(usageEvents: ArrayList<UsageEvent>): String {
        var cal_begin = Calendar.getInstance()
        cal_begin.set(Calendar.HOUR, -1)
        val gson = Gson()
        val json: String = gson.toJson(usageEvents)
        return json
    }

    data class UsageEvent(
        var resultTime: String,
        var packageName: String,
        var timeStamp: String,
        var eventType: String
    )

}