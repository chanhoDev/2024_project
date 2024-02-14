package com.chanho.motion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chanho.motion.databinding.ActivityMotionBinding

class MotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMotionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBindView()
        onObserve()
    }
    private fun onBindView() {
        with(binding) {
            serviceBtn.setOnClickListener {
                val intent = Intent(this@MotionActivity, MotionService::class.java)
                ContextCompat.startForegroundService(this@MotionActivity, intent)
            }
            serviceStopBtn.setOnClickListener {
                val intent = Intent(this@MotionActivity, MotionService::class.java)
                stopService(intent)
            }
        }
    }
    private fun onObserve() {

    }

}