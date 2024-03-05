package com.chanho.motion

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import com.google.gson.Gson
import java.util.Calendar
import java.util.Date

class SampleWorkerAppUsage(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    companion object {
        const val PREV_INITIAL_DATE = "prevInitialDate"
    }

    override fun doWork(): Result {
        val prevCal = Calendar.getInstance()
        prevCal.set(Calendar.MINUTE, -15)
        var prevInitialDate = PrefHelper[PREV_INITIAL_DATE, Util.dateFormat.format(prevCal.time)]
        val currentCal = Calendar.getInstance()
        val currentDate = Util.dateFormat.format(currentCal.time)

        val usageStateList = getAppUsageStats(prevInitialDate, currentDate)
        showAppUsageStats(usageStateList, prevInitialDate, currentDate)
        getForegroundActivity(context, prevInitialDate, currentDate)

        prevInitialDate = Util.dateFormat.format(currentCal.time)
        PrefHelper[PREV_INITIAL_DATE] = prevInitialDate
        return Result.success()
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
                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
                        event = eventAux
                        packageName = event.getPackageName()
                        var date = Date(event.timeStamp)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        var resultTime = Util.dateFormat.format(cal.time)
                        usageEventList.add(
                            UsageEvent(
                                resultTime = resultTime,
                                packageName = packageName,
                                timeStamp = "${Util.dateFormat.format(Date(event.timeStamp))}",
                                eventType = "ACTIVITY_PAUSED"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
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
                                eventType = "ACTIVITY_RESUMED"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.ACTIVITY_STOPPED) {
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
                                eventType = "ACTIVITY_STOPPED"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.CONFIGURATION_CHANGE) {
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
                                eventType = "CONFIGURATION_CHANGE"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.DEVICE_SHUTDOWN) {
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
                                eventType = "DEVICE_SHUTDOWN"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.DEVICE_STARTUP) {
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
                                eventType = "DEVICE_STARTUP"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.FOREGROUND_SERVICE_START) {
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
                                eventType = "FOREGROUND_SERVICE_START"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.FOREGROUND_SERVICE_STOP) {
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
                                eventType = "FOREGROUND_SERVICE_STOP"
                            )
                        )
                    }
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
                    if (eventAux.getEventType() == UsageEvents.Event.NONE) {
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
                                eventType = "NONE"
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

                    if (eventAux.getEventType() == UsageEvents.Event.SHORTCUT_INVOCATION) {
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
                                eventType = "SHORTCUT_INVOCATION"
                            )
                        )
                    }
                    if (eventAux.getEventType() == UsageEvents.Event.STANDBY_BUCKET_CHANGED) {
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
                                eventType = "STANDBY_BUCKET_CHANGED"
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
                PrefHelper["${Util.dateFormat.format(cal_start.time)} ~ ${
                    Util.dateFormat.format(
                        cal_end.time
                    )
                }_EVENT"] = saveEventData(usageEventList)
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

    private fun saveUsageStateData(usageState: ArrayList<UsageState>): String {
        val gson = Gson()
        val json: String = gson.toJson(usageState)
        return json
    }

    private fun showAppUsageStats(
        usageStatsMap: Map<String, UsageStats>,
        prevInitialDate: String,
        currentDate: String,
    ) {
        var usageStateList = ArrayList<UsageState>()
        val cal_end = Calendar.getInstance()
        cal_end.time = Util.dateFormat.parse(currentDate)
        var cal_start = Calendar.getInstance()
        cal_start.time = Util.dateFormat.parse(prevInitialDate)
        usageStatsMap.forEach {
            val key = it.key
            val usageState = it.value
            usageStateList.add(
                UsageState(
                    key = key,
                    firstTimeStamp = " ${Util.dateFormat.format(Date(usageState.getFirstTimeStamp()))} ",
                    lastTimeStamp = "${Util.dateFormat.format(Date(usageState.getLastTimeStamp()))}",
                    packageName = "${usageState.getPackageName()} ",
                    totalTimeInForeground = "${usageState.getTotalTimeInForeground()}",
                    lastTimeUsed = "${usageState.getLastTimeUsed()}"
                )
            )
        }
        PrefHelper["${Util.dateFormat.format(cal_start.time)} ~ ${Util.dateFormat.format(cal_end.time)}_USAGE_STATS_MAP"] =
            saveUsageStateData(usageStateList)
    }

    private fun getAppUsageStats(
        prevInitialDate: String,
        currentDate: String
    ): Map<String, UsageStats> {
        val cal = Calendar.getInstance()
        var startTime = prevInitialDate.let {
            cal.time = Util.dateFormat.parse(prevInitialDate)
            cal.timeInMillis
        }
        val currentCal = Calendar.getInstance()
        currentCal.time = Util.dateFormat.parse(currentDate)

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val queryUsageStats = usageStatsManager.queryAndAggregateUsageStats(
            startTime, currentCal.timeInMillis // 3
        )
        return queryUsageStats
    }

    data class UsageEvent(
        var resultTime: String,
        var packageName: String,
        var timeStamp: String,
        var eventType: String
    )

    data class UsageState(
        var key: String,
        var firstTimeStamp: String,
        var lastTimeStamp: String,
        var packageName: String,
        var totalTimeInForeground: String,
        var lastTimeUsed: String
    )


}