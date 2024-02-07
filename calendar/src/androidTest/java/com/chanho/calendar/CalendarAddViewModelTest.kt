package com.chanho.calendar

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chanho.common.data.AlarmDao

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * CalendarAddViewModelTest 테스트 목록
 *  와락상품을_구매했는지_확인한다_와락상품은_구매한것과_관계없이_다음단계로_넘어간다
 *  단건결제인_기억검사를_구매했는지_확인한다_기억검사_상품은_구매한것과_관계없이_다음단계로_넘어간다
 *  정기결제인_와락라디오를_구매했는지_확인한다_와락라디오_상품은_본인이_직접_구매한_경우이거나_상태가_활성화_되어있는경우_다음단계로_안넘어간다
 *
 * 테스트 전 유의사항
 *  key_access_token 값을 갱신해서 사용해야함
 */


@RunWith(AndroidJUnit4::class)
class CalendarAddViewModelTest {

    private lateinit var viewModel:CalendarAddViewModel
    lateinit var application: Application

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        application = ApplicationProvider.getApplicationContext()
//        viewModel = CalendarAddViewModel(application,
//            alarmDao = )
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.chanho.calendar.test", appContext.packageName)
    }
}