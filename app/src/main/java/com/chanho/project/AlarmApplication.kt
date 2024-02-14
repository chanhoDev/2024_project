package com.chanho.project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import com.chanho.common.Constants
import com.chanho.common.PrefHelper
import com.chanho.common.data.AlarmDatabase
import com.chanho.common.data.AlarmEntity
import com.chanho.project.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
@HiltAndroidApp
class AlarmApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        PrefHelper.init(this)
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            getSystemService(NotificationManager::class.java).run {
                val forChannel = NotificationChannel(
                    Constants.FORE_CHANNEL_ID,
                    "Foreground",
                    NotificationManager.IMPORTANCE_HIGH
                )
                createNotificationChannel(forChannel)
            }
        }

    }
}