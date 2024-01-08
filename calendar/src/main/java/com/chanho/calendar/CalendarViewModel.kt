package com.chanho.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chanho.common.SingleLiveEvent
import com.chanho.common.Util
import com.chanho.common.data.AlarmDao
import com.chanho.common.data.AlarmEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel  @Inject constructor(
    private val application: Application,
    private val alarmDao: AlarmDao
) : AndroidViewModel(application) {

    private val _ampmResult = MutableLiveData<String>()
    val ampmResult: LiveData<String> = _ampmResult

}




