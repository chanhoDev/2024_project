package com.chanho.project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chanho.calendar.CalendarActivity
import com.chanho.camera.CameraActivity
import com.chanho.graph.GraphActivity
import com.chanho.localization.LocalizationActivity
import com.chanho.motion.MotionActivity
import com.chanho.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.alarmBtn.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
        }
        binding.dbLocalizationBtn.setOnClickListener {
            val intent = Intent(this, LocalizationActivity::class.java)
            startActivity(intent)
        }
        binding.graphBtn.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }
        binding.calendarBtn.setOnClickListener {
            val intent = Intent(this,CalendarActivity::class.java)
            startActivity(intent)
        }
        binding.motionBtn.setOnClickListener {
            val intent = Intent(this, MotionActivity::class.java)
            startActivity(intent)
        }
        binding.cameraBtn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

    }

}