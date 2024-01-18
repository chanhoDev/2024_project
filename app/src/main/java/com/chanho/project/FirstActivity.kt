package com.chanho.project

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.chanho.calendar.CalendarActivity
import com.chanho.common.CommonNotification
import com.chanho.common.data.AlarmDatabase
import com.chanho.imagerolling.ImageScrollingActivity
import com.chanho.project.databinding.ActivityFirstBinding
import com.chanho.project.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class FirstActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFirstBinding
    private var rewardedAd: RewardedAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"
    private var loginStatus = false

    @Inject
    lateinit var database: AlarmDatabase
    private lateinit var providers: ArrayList<AuthUI.IdpConfig>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val navController = findNavController(R.id.nav_host_fragment_content_main)

        MobileAds.initialize(this) {
            Toast.makeText(this, "ad mob 테스트입니다.", Toast.LENGTH_SHORT).show()
        }
        loadBaaner()

        //firebase가 제공하는 이메일로그인 ui 가져오기
        providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        //현재 로그인이 안 되어 있으면 로그인 액티비티로 이동
        if (FirebaseAuth.getInstance().currentUser == null) {
            logIn()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    // 이미 권한을 거절한 경우 권한 설정 화면으로 이동
                    Toast.makeText(this, "권한설정", Toast.LENGTH_SHORT).show()
                    val intent: Intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                            Uri.parse("package:" + packageName)
                        )
                    startActivity(intent)
                    finish()
                } else {
                    // 처음 권한 요청을 할 경우
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                        when (it) {
                            true -> {
                                Toast.makeText(this, "권한요청1", Toast.LENGTH_SHORT).show()
                            }

                            false -> {
                                Toast.makeText(this, "권한요청2", Toast.LENGTH_SHORT).show()
                                moveTaskToBack(true)
                                finishAndRemoveTask()
                                exitProcess(0)
                            }
                        }
                    }.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        CommonNotification.createNotificationForAlarmChannel(
            this
        )

        with(binding) {
            setLogInOutBtnStatus(loginStatus)
            logout.setOnClickListener {
                if(loginStatus){
                    signOut()
                }else{
                    logIn()
                }
            }
        }


//        binding.fullAddText.text = "전면 광고 테스트 버튼 "
//        binding.fullAddText.setOnClickListener {
//            if (mInterstitialAd != null) {
//                mInterstitialAd?.show(requireActivity())
//            } else {
//                Log.e("loadFullAdd", "The interstitial ad wasn't ready yet.")
//            }
//        }
//        loadFullAdd()
//        binding.nativeAddText.text = "네이티브 광고"
//
//        binding.rewardAddText.text="보상형 광고 테스트 버튼 "
//        binding.rewardAddText.setOnClickListener {
//            rewardedAd?.let { ad ->
//                ad.show(requireActivity(), OnUserEarnedRewardListener { rewardItem ->
//                    // Handle the reward.
//                    val rewardAmount = rewardItem.amount
//                    val rewardType = rewardItem.type
//                    Log.d(TAG, "User earned the reward. rewardAmount = ${rewardAmount} rewardType = ${rewardType}")
//                })
//            } ?: run {
//                Log.d(TAG, "The rewarded ad wasn't ready yet.")
//            }
//        }
//        loadRewardAdd()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

//    private fun setDatabase() {
//        val alarmCode = database.AlarmDao().getAll().takeIf { !it.isNullOrEmpty() }?.let {
//            it.last().id + 1
//        } ?: run {
//            1
//        }
//
//        lifecycleScope.launch {
//            database.AlarmDao().insertAll(
//                AlarmEntity(
//                    alarmTime = "2024-01-02 16:29:00",
//                    alarmContent = "테스트",
//                    alarmCode = alarmCode
//                )
//            )
//        }
//        Log.e("setDatabaseResult", alarmCode.toString())
//    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    fun logIn() {
        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    //로그인 결과
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        loginStatus = (result.resultCode == RESULT_OK)
        setLogInOutBtnStatus(loginStatus)

    }

    //로그아웃
    fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    loginStatus = false
                    setLogInOutBtnStatus(false)
                }
            }
    }

    fun setLogInOutBtnStatus(status:Boolean){
        binding.logout.text = if(status){
            "로그아웃"
        }else{
            "로그인"
        }
    }

    private fun loadRewardAdd() {
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = ad
                }
            })
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

    }

    private fun loadFullAdd() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d("loadFullAdd", it) }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.e("loadFullAdd", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("loadFullAdd", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("loadFullAdd", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("loadFullAdd", "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("loadFullAdd", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("loadFullAdd", "Ad showed fullscreen content.")
            }
        }
    }

    private fun loadBaaner() {
        MobileAds.initialize(this) {}
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                Log.e("loadBanner", "onAddClicked")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.e("loadBanner", "onAdClosed")

            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("loadBanner", "onAdFailedToLoad = ${p0.toString()}")

            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.e("loadBanner", "onAdImpression")

            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e("loadBanner", "onAdLoaded")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.e("loadBanner", "onAdOpened")
            }

            override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
                Log.e("loadBanner", "onAdSwipeGestureClicked")
            }
        }
    }
}