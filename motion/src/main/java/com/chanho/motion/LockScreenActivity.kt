package com.chanho.motion

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chanho.common.PrefHelper
import com.chanho.common.Util
import java.util.Calendar

class LockScreenActivity : AppCompatActivity() {

    companion object {
        const val LOCK_SCREEN = "lock_screen"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O_MR1){
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        findViewById<TextView>(R.id.textview).setOnClickListener {
            val currentCal = Calendar.getInstance()
            val currentDate = Util.dateFormat.format(currentCal.time)
            PrefHelper["${currentDate}_$LOCK_SCREEN"] = "$currentDate"
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"꺼져 onBackPressed",Toast.LENGTH_SHORT).show()
//        val currentCal = Calendar.getInstance()
//        val currentDate = Util.dateFormat.format(currentCal.time)
//        PrefHelper["${currentDate}_$LOCK_SCREEN"] = "$currentDate"
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"꺼져 onDestroy",Toast.LENGTH_SHORT).show()
    }
}