package com.chanho.motion

import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.chanho.common.PrefHelper

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * MotionServiceTest 테스트 목록
 *  모션서비스가_destroy가_되어도_다시_실행이된다
 *
 * 테스트 전 유의사항
 *  key_access_token 값을 갱신해서 사용해야함
 */
@RunWith(AndroidJUnit4::class)
class MotionServiceTest {

    private val context = InstrumentationRegistry.getInstrumentation().context
    private lateinit var motionService: MotionService

    @get:Rule
    val serviceRule = ServiceTestRule()
    @Before
    fun setup() {
        motionService = MotionService()
        val intentFilter = IntentFilter().apply {
            addAction("alarm_receiver_test")
        }
//        InstrumentationRegistry.getInstrumentation().targetContext.registerReceiver(
//            receiver,
//            intentFilter
//        )
        PrefHelper.init(context)
    }

    @Test
    fun `모션서비스가_destroy가_되어도_다시_실행이된다`() {
//        val intent = Intent("alarm_receiver_test").apply {
//            putExtra(Constants.ALARM_TYPE, Constants.AlarmPopupType.MEDICATION)
//            putExtra(Constants.CONTENT, "컨텐츠")
//            putExtra(Constants.ALARM_TIME, "2023-02-22 12:22:00")
//        }
//        context.sendOrderedBroadcast(intent, null)
//        Thread.sleep(2000)
//        assertEquals(receiver.content, "컨텐츠")
//        assertEquals(receiver.alarmTime, "2023-02-22 12:22:00")

        val intent = Intent(context, MotionService::class.java)
//        ContextCompat.startForegroundService(context, intent)
        serviceRule.startService(intent)
        context.stopService(intent)


    }
}