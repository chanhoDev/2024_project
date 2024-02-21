package com.chanho.motion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.R
import com.chanho.common.Util
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import java.util.Calendar

const val USER_ACTIVITY_SERVICE=4

class UserActivityBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)!!
            var setString = PrefHelper[DETECT, setOf<String>()]
            val cal = Calendar.getInstance()
            val time = Util.dateFormat.format(cal.time)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(context, Constants.ALARM_CHANNEL)
            } else {
                NotificationCompat.Builder(context)
            }
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    Constants.ALARM_CHANNEL,
                    "알림",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                })

            for (event in result.transitionEvents) {
                when (event.activityType) {
                    DetectedActivity.ON_FOOT -> {
                        notificationManager.notify(
                            USER_ACTIVITY_SERVICE, builder
                                .setSmallIcon(R.drawable.ic_logo_png)
                                .setContentTitle("user_activity")
                                .setContentText("[time=$time ,ON_FOOT,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                                .build()
                        )
                        setString = setString.plus("[time=$time ,ON_FOOT,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Log.e("userTransitionEvent", "[time=$time ,ON_FOOT,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Toast.makeText(context, "userEvent_ON_FOOT", Toast.LENGTH_SHORT).show()
                    }
                    DetectedActivity.STILL -> {
                        notificationManager.notify(
                            USER_ACTIVITY_SERVICE, builder
                                .setSmallIcon(R.drawable.ic_logo_png)
                                .setContentTitle("user_activity")
                                .setContentText("[time=$time ,STILL,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                                .build()
                        )
                        setString = setString.plus("[time=$time ,STILL,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Log.e("userTransitionEvent", "[time=$time ,STILL,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Toast.makeText(context, "userEvent_STILL", Toast.LENGTH_SHORT).show()
                    }

                    DetectedActivity.WALKING -> {
                        notificationManager.notify(
                            USER_ACTIVITY_SERVICE, builder
                                .setSmallIcon(R.drawable.ic_logo_png)
                                .setContentTitle("user_activity")
                                .setContentText("[time=$time ,WALKING,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                                .build()
                        )
                        setString = setString.plus("[time=$time ,WALKING,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Log.e("userTransitionEvent", "[time=$time ,WALKING,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Toast.makeText(context, "userEvent_WALKING", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        notificationManager.notify(
                            USER_ACTIVITY_SERVICE, builder
                                .setSmallIcon(R.drawable.ic_logo_png)
                                .setContentTitle("user_activity")
                                .setContentText("[time=$time ,ELSE,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                                .build()
                        )
                        setString = setString.plus("[time=$time ,ELSE,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Log.e("userTransitionEvent", "[time=$time ,ELSE,transitionType = ${transitionType(event.transitionType)},elapsedRealTimeNanos =${event.elapsedRealTimeNanos}]")
                        Toast.makeText(context, "userEvent_ELSE", Toast.LENGTH_SHORT).show()
                    }
                }
                PrefHelper[DETECT] = setString
            }
        }
    }

    fun transitionType(transitionType: Int): String {
        return when (transitionType) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                "ACTIVITY_TRANSITION_ENTER"
            }

            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                "ACTIVITY_TRANSITION_EXIT"
            }

            else -> {
                "ACITIVITY_NULL"
            }
        }
    }

    companion object {
        val DETECT = "detect_version_15:53"
    }
}