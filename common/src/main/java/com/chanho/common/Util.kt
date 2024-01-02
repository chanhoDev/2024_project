package com.chanho.common

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.*
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


object Util {
    var IS_DEVELOPER_MODE = false
    var DEVELOPER_MODE_CLICK_COUNT = 0
    val DEVELOPER_MODE_MAX_CLICK_COUNT = 10

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val simpleDateWithDayAndEFormat = SimpleDateFormat("dd E", Locale.KOREA)
    val simpleDateFormatYearAndMonth = SimpleDateFormat("yyyy년 MM월", Locale.KOREA)
    val simpleDateFormatYearMonthDay = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)

    val simpleDateFormatYearAndMonthDayE = SimpleDateFormat("yyyy년 MM월dd일 (E)", Locale.KOREA)
    val simpleDateFormatNoDash = SimpleDateFormat("yyyyMMdd", Locale.KOREA)

    val dateFormatForTime = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
    val dateFormat_without_second = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
    val dateFormate_time_type = SimpleDateFormat("aa hh:mm", Locale.KOREA)
    val dateFormate_korea_time_type = SimpleDateFormat("aa hh시mm분", Locale.KOREA)
    val dateFormate_24_hour = SimpleDateFormat("HH:mm", Locale.KOREA)
    val localDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
    val dateFormate_date_dot = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    val dateFormate_date_time_dot = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)


    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            val pm: PackageManager = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo("" + packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                pm.getPackageInfo("" + packageName, PackageManager.GET_META_DATA)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getUserNameLast2Character(name: String): String {
        return name.subSequence(0.coerceAtLeast(name.length - 2), name.length).toString()
    }

    fun getMobileNumber(number: String): String {
        val formattedNumber: String = PhoneNumberUtils.formatNumber(number, "KR")
        return if (formattedNumber.startsWith("+") && formattedNumber.indexOf(" ") > 0) {
            formattedNumber.substring(formattedNumber.indexOf(" ")).replace("-".toRegex(), "")
        } else if(formattedNumber.startsWith("+82")) {
            formattedNumber.replace("+82", "0")
        }
        else {
            formattedNumber.replace("-".toRegex(), "")
        }
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    fun pxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
    }

    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun adjustFontScale(
        baseContext: Context,
        context: Context,
        configure: Configuration,
        fontScale: Float = 1f,
    ) {
        configure.fontScale = fontScale
        val metrics: DisplayMetrics = context.resources.displayMetrics
        metrics.scaledDensity = configure.fontScale * metrics.density
        baseContext.createConfigurationContext(configure)
    }

    fun fontScaleToPosition(fontScale: Float): Int {
        return when {
            fontScale < 1.0f -> 1
            fontScale < 1.2f -> 2
            fontScale < 1.4f -> 3
            else -> 2
        }
    }

    fun positionToFontScale(position: Int): Float {
        return when (position) {
            1 -> 0.8f
            2 -> 1.0f
            3 -> 1.2f
            else -> 1.0f
        }
    }

    fun String.toSpanned(): Spanned {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    }

    fun makeBannerImageUrl(imagePath:String):String{
        return "https://rl3tmy14s.toastcdn.net/$imagePath"
    }

    fun LinearLayoutManager.smoothScrollToPositionWithDelay(position: Int, context: Context) {
        Handler(Looper.getMainLooper()).postDelayed({
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }, 300)
    }

    /**
     *
     * 한글 조사(助詞) 연결 (을/를,이/가,은/는,로/으로)
     *
     * 한글에 단어를 연결하는 조사(助詞) 는 앞에 오는 체언(體言)에 따라 변화되는 경우에 사용
     *
     * 규칙
     *
     * 1. 체언의 종성에 받침이 있는 경우 '을/이/은/으로/과'
     *
     * 2. 체언의 종성에 받침이 없는 경우 '를/가/는/로/와'
     *
     * 3. ‘로/으로'의 경우 체언 종성의 받침이 ‘ㄹ' 인경우 '로’ 아니면 '으로’
     *
     * @param name
     *
     * @param firstValue
     *
     * @param secondValue
     *[一-龥]
     * @return
     */
    fun getPostWord(name: String, firstValue: String, secondValue: String?): String {
        val regex = Regex("[一-龥/(/)]")
        val replaceName = regex.replace(name,"")
        val lastName = replaceName[replaceName.length - 1]

        if (lastName.code < 0xAC00 || lastName.code > 0xD7A3) {
            return ""
        }
        val seletedValue = if ((lastName.code - 0xAC00) % 28 > 0) firstValue else secondValue!!
        return seletedValue
    }

    fun setAlarmMusic(context: Context) {
        val audioManager: AudioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.let {
            Log.e("audioManager", "${it.ringerMode}")
            when (it.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> {
                    //사일런트 모드일 경우
                }
                AudioManager.RINGER_MODE_VIBRATE -> {
                    // 진동모드일 경우
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager: VibratorManager =
                            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        var vibrator = vibratorManager.defaultVibrator
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                1000,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        var vibrator =
                            context
                                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                1000,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        var vibrator =
                            context
                                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(1000)
                    }
                }
                AudioManager.RINGER_MODE_NORMAL -> {
                    //벨 모드일 경우
                    val notification: Uri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    var ringtone =
                        RingtoneManager.getRingtone( context, notification)
                    val audioAttributes: AudioAttributes =
                        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                    ringtone.audioAttributes = audioAttributes
                    ringtone.play()
                }
                else -> {

                }
            }
        }

    }

    fun getSanitizer(uri:String): UrlQuerySanitizer {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true
        sanitizer.parseUrl(uri)
        return sanitizer
    }

    /**
     * 단위 테스트 시 LiveData 결과 변경 감지 용도.
     * Application 에서는 사용 하지 말것.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            this@getOrAwaitValue.observeForever(observer)
        }

        try {
            afterObserve.invoke()
            // Don't wait indefinitely if the LiveData is not set.
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            CoroutineScope(Dispatchers.Main).launch {
                this@getOrAwaitValue.removeObserver(observer)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    fun isNewYear(checkDate: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = checkDate
        println("isNewYear calendar = ${java.text.SimpleDateFormat.getInstance().format(calendar.time)}")
        println("isNewYear result = ${calendar.get(Calendar.MONTH)+1 == 12}")
        return calendar.get(Calendar.MONTH)+1 == 12
    }

}
