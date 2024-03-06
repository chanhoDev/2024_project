package com.chanho.motion

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import com.chanho.common.Constants


class ScreenLockBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var manager = context.getSystemService(NotificationManager::class.java)
        var notiBuilder = NotificationCompat.Builder(context, Constants.FORE_CHANNEL_ID)
            .setContentText("잠금화면체크")
            .setSmallIcon(com.chanho.common.R.drawable.ic_launcher_waplat)
        var keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isDeviceSecure()) {
            Log.e("사용자", "잠금설정이 되어있음 ")
            notiBuilder.setContentTitle("잠금설정이 되어있음")
            if (intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                Log.e("사용자", "ACTION_USER_PRESENT")
            }
            notiBuilder.setContentText("ACTION_USER_PRESENT")
            manager.notify(SERVICE_ID3, notiBuilder.build())
        } else {
            Log.e("사용자", "잠금설정이 안되어있음 !")
            notiBuilder.setContentTitle("잠금설정이 안되어있음")
            notiBuilder.setContentText("잠금설정이 안되어있음")
            manager.notify(SERVICE_ID3, notiBuilder.build())
        }

        if (intent.action.equals(Intent.ACTION_SCREEN_ON)) {
            val lockIntent = Intent(context,LockScreenActivity::class.java)
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(lockIntent)
        }
    }
}