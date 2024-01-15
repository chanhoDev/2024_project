package com.chanho.project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chanho.localization.LocalizationActivity
import com.chanho.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.alarmBtn.setOnClickListener {
            val intent = Intent(this,FirstActivity::class.java)
            startActivity(intent)
        }
        binding.dbLocalizationBtn.setOnClickListener {
            val intent = Intent(this, LocalizationActivity::class.java)
            startActivity(intent)
        }

    }

}