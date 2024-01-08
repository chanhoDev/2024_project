package com.chanho.common

import android.app.Notification
import android.app.Notification.Style
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
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
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import kotlin.contracts.contract


object CommonNotification {

    private val vibrationPattern = longArrayOf(
        0, 2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
        2000, 500, 1000, 500, 1000, 500, 1000, 500,
    )

    fun deliverNotificationForAlarm(
        context: Context,
        alarmCode: Int,
        title: String? = "",
        body: String? = "",
        contentPendingIntent: PendingIntent,
        fullScreenPendingIntent: PendingIntent
    ) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val action = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.color.green_00A88A),
            context.getString(R.string.confirm),
            contentPendingIntent
        ).build()


        val builder = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            NotificationCompat.Builder(context, Constants.ALARM_CHANNEL)
        } else {
            NotificationCompat.Builder(context)
        }
        notificationManager.notify(
            alarmCode, builder
                .setSmallIcon(R.drawable.ic_logo_png)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(vibrationPattern)
                .setAutoCancel(true)
                .addAction(action)
                .build()
        )
    }

    fun createNotificationForAlarmChannel(
        context:Context
    ) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    Constants.ALARM_CHANNEL,
                    "복약/일정 알림",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                    description = "복약/일정 알림을 위한 채널 입니다."
                    vibrationPattern = vibrationPattern
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                        audioAttributes
                    )
                })
        }
    }
}