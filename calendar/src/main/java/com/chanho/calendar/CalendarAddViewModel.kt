package com.chanho.calendar

import android.app.Application
import android.os.Parcelable
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chanho.common.SingleLiveEvent
import com.chanho.common.data.AlarmDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class CalendarAddViewModel @Inject constructor(
    private val application: Application,
    private val alarmDao: AlarmDao
) : AndroidViewModel(application) {

    val calendarData = MutableLiveData(CalendarData())

    private val _resultCalendarData = SingleLiveEvent<CalendarData>()
    val resultCalendarData: LiveData<CalendarData> = _resultCalendarData

    private val _saveBtnStatus = MutableLiveData<Boolean>()
    val saveBtnStatus: LiveData<Boolean> = _saveBtnStatus

    private val _calendarBtnStatus = MutableLiveData<Boolean>()
    val calendarBtnStatus: LiveData<Boolean> = _calendarBtnStatus

    private val _repeatDayModelList = MutableLiveData<List<RepeatDayModel>>()
    val repeatDayModelList: LiveData<List<RepeatDayModel>> = _repeatDayModelList

    private val _displayCalendarData = MutableLiveData<String>()
    val displayCalendarData: LiveData<String> = _displayCalendarData

    private val _displayInadvanceData = MutableLiveData<String>()
    val displayInadvanceData: LiveData<String> = _displayInadvanceData

    private val _inadvanceList = MutableLiveData<List<InadvanceModel>>()
    val inadavanceList: LiveData<List<InadvanceModel>> = _inadvanceList

    fun onLoadData() {
        _repeatDayModelList.value = listOf(
            RepeatDayModel("일", false),
            RepeatDayModel("월", false),
            RepeatDayModel("화", false),
            RepeatDayModel("수", false),
            RepeatDayModel("목", false),
            RepeatDayModel("금", false),
            RepeatDayModel("토", false),
        )
    }


    //저장 이벤트 처리
    fun onSaveCalendar() {
        calendarData.value?.takeIf { (it.content.isNotEmpty()) && (it.date.isNotEmpty()) && (it.time.isNotEmpty()) && (it.dayOfWeek.isNotEmpty()) }
            ?.let {
                _resultCalendarData.value = it
            }
    }

    fun checkBtnStatus() {
        Log.e("checkBtnStatus", calendarData.value.toString())
        calendarData.value?.takeIf { (it.content.isNotEmpty()) && (it.date.isNotEmpty()) && (it.time.isNotEmpty()) && (it.dayOfWeek.isNotEmpty()) }
            ?.let {
                _saveBtnStatus.value = true
            } ?: run {
            _saveBtnStatus.value = false
        }
    }

    fun onRepeatItemClicked(repeatDayModel: RepeatDayModel) {
        _repeatDayModelList.value = _repeatDayModelList.value?.map {
            if (it.dayOfWeek == repeatDayModel.dayOfWeek) {
                it.copy(isClick = !it.isClick)
            } else {
                it
            }
        }.also { repeatDayModelList ->
            calendarData.value?.let {
                calendarData.value = it.copy(dayOfWeek = repeatDayModelList?.toSet()?: emptySet())
            }
        }
    }

    fun saveCalendarData(data: CalendarData) {
        Log.e("saveCalendarData", data.toString())
        calendarData.value = data
        _displayCalendarData.value = "${data.date} / ${data.time}"
    }

    fun setInadavanceList() {
        _inadvanceList.value =
            InadvanceType.values().map { InadvanceModel(inadavanceType = it, isClick = false) }
    }

    fun onInadvanceClick(item: InadvanceModel) {
        _inadvanceList.value?.let {
            _inadvanceList.value = it.map {
                if (it.inadavanceType == item.inadavanceType) {
                    it.copy(isClick = !it.isClick)
                } else {
                    it.copy(isClick = false)
                }
            }
        }
    }

    fun saveInadvance() {
        _inadvanceList.value?.find { it.isClick }?.let { inadvanceModel ->
            Log.e("saveInadvance", inadvanceModel.toString())
            calendarData.value?.let {
                calendarData.value =
                    it.copy(inAdvanceAlarm = inadvanceModel.inadavanceType.typeName)
                _displayInadvanceData.value = inadvanceModel.inadavanceType.typeName
            }
        }

    }


}

@Parcelize
data class CalendarData(
    var content: String = "",
    var date: String = "",
    var time: String = "",
    var dayOfWeek: Set<RepeatDayModel> = setOf(),
    var inAdvanceAlarm: String = "",
    var alarmOnAndOff: Boolean = true
) : Parcelable




