package com.chanho.common

import android.app.Notification.Style
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import kotlin.contracts.contract


object CommonNotification {

    fun deliverNotificationForAlarm(
        context: Context, alarmCode: Int,
        title: String? = "",
        body: String? = "",
        contentPendingIntent: PendingIntent,
        fullScreenPendingIntent:PendingIntent
    ) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val action = NotificationCompat.Action.Builder(
            // The icon that will be displayed on the button (or not, depends on the Android version)
            IconCompat.createWithResource( context , R.color.green_00A88A),
            // The text on the button
            context.getString(R.string.confirm),
            // The action itself, PendingIntent
            contentPendingIntent
        ).build()
        val caller = Person.Builder()
            // Caller icon
            .setIcon(IconCompat.createWithResource(context,R.drawable.ic_launcher_waplat))
            // Caller name
            .setName("Chuck Norris")
            .setImportant(true)
            .build()

        notificationManager.notify(
            alarmCode, NotificationCompat.Builder(
                context,
                alarmCode.toString()
            )
                .setSmallIcon(R.drawable.ic_logo_png)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent,true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTimeoutAfter(60000) // 1분
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true)
//                .setStyle(NotificationCompat.CallStyle.forIncomingCall(
//                    caller,contentPendingIntent,contentPendingIntent
//                ))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_launcher_waplat))
//                .addAction(1,getActionText(context,R.string.confirm,R.color.grey_700),contentPendingIntent)
//                .addAction(NotificationCompat.Action(R.drawable.bg_checkbox_selector,"확인하기!!",contentPendingIntent))
                .addAction(action)
//                .addAction(action)
//                .addAction(action)
                .build()
        )
    }

    private fun getActionText(context: Context,@StringRes stringRes: Int, @ColorRes colorRes: Int): Spannable? {
        val spannable: Spannable = SpannableString(context.getText(stringRes))
        if (VERSION.SDK_INT >= VERSION_CODES.N_MR1) {
            spannable.setSpan(
                ForegroundColorSpan(context.getColor(colorRes)), 0, spannable.length, 0
            )
        }
        return spannable
    }


    fun deliverNotification(
        context: Context, channelId: String = "",
        title: String? = "",
        body: String? = "",
        notificationManager: NotificationManager,
        contentIntent: Intent
    ) {
        val requestID = System.currentTimeMillis().toInt()
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            requestID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notificationManager.notify(
            requestID, NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(R.drawable.ic_logo_png)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL).build()
        )
    }

    fun createNotificationForAlarmChannel(
        channelId: String,
        notificationManager: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "이름:$channelId",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern= longArrayOf(1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000)
                    description = "설명:$channelId"
                })
        }
    }

    fun createNotificationChannel(
        channelId: String,
        notificationManager: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "$channelId",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                })
        }
    }
}