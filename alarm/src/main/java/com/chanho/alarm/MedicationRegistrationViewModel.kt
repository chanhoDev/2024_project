package com.chanho.alarm

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
class MedicationRegistrationViewModel  @Inject constructor(
    private val application: Application,
    private val alarmDao: AlarmDao
) : AndroidViewModel(application) {

    private val _ampmResult = MutableLiveData<String>()
    val ampmResult: LiveData<String> = _ampmResult

    private val _hourResult = MutableLiveData<String>()
    val hourResult: LiveData<String> = _hourResult

    private val _minuteResult = MutableLiveData<String>()
    val minuteResult: LiveData<String> = _minuteResult

    private val _showDialogSetting = MutableLiveData<TimeType>()
    val showDialogSetting: LiveData<TimeType> = _showDialogSetting

    private val _optionModelList = MutableLiveData<List<OptionModel>>()
    val optionModelList: LiveData<List<OptionModel>> = _optionModelList

    private val _onRegistrationClick = MutableLiveData<String>()
    val onRegistrationClick: LiveData<String> = _onRegistrationClick

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _registerBtnStatus = MutableLiveData<Boolean>()
    val registerBtnStatus: LiveData<Boolean> = _registerBtnStatus

    private val _isDialogInit = SingleLiveEvent<Int>()
    val isDialogInit: LiveData<Int> = _isDialogInit

    private var _ampmOptionModelList = listOf<AmpmOptionModel>()
    private var _hourOptionModelList = listOf<HourOptionModel>()
    private var _minuteOptionModelList = listOf<MinuteOptionModel>()

    fun onLoadTimeDataList(
        ampmList: List<String>,
        hourList: List<String>,
        minuteList: List<String>
    ) {
        _ampmOptionModelList =
            convertToOptionModelList(
                ampmList,
                timeType = TimeType.AMPM
            ) as List<AmpmOptionModel>

        _hourOptionModelList =
            convertToOptionModelList(
                hourList,
                timeType = TimeType.HOUR
            ) as List<HourOptionModel>

        _minuteOptionModelList = convertToOptionModelList(
            minuteList,
            timeType = TimeType.MINUTE
        ) as List<MinuteOptionModel>

        initLayout()

    }

    private fun initLayout() {
        val cal = Calendar.getInstance()
        var ampm = if (cal.get(Calendar.AM_PM) == 0) {
            application.getString(com.chanho.common.R.string.common_am)
        } else {
            application.getString(com.chanho.common.R.string.common_pm)
        }
        var hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)

        _hourResult.value = if (minute > 30) {
            var tempHour = ((hour + 1) % 12)
            if (tempHour == 0) {
                tempHour = 12
                ampm = if (cal.get(Calendar.AM_PM) == 0) {
                    application.getString(com.chanho.common.R.string.common_pm)
                } else {
                    application.getString(com.chanho.common.R.string.common_am)
                }
            }
            hour = tempHour
            String.format("%02d", hour)
        } else {
            var tempHour = (hour % 12)
            if (tempHour == 0) {
                tempHour = 12
                hour = tempHour
            }
            String.format("%02d", hour)
        }
        _ampmResult.value = ampm
        _minuteResult.value = if (minute > 30) {
            "00"
        } else {
            "30"
        }

        _ampmOptionModelList = _ampmOptionModelList.map {
            it.copy(isClick = it.content == _ampmResult.value)
        }
        _hourOptionModelList = _hourOptionModelList.map {
            it.copy(isClick = it.content == _hourResult.value.toString())
        }
        _minuteOptionModelList = _minuteOptionModelList.map {
            it.copy(isClick = it.content == _minuteResult.value.toString())
        }
    }

    fun onRegistrationClick() {
        var ampm: String? = null
        var hour: String? = null
        var minute: String? = null
        _ampmResult.value?.let {
            ampm = it
        } ?: run {
            return
        }
        _hourResult.value?.let {
            hour = it
        } ?: run {
            return
        }
        _minuteResult.value?.let {
            minute = it
        } ?: run {
            return
        }
        addAlarmTime("$ampm $hour:$minute")
    }

    private fun convertToOptionModelList(
        stringList: List<String>,
        timeType: TimeType
    ): List<OptionModel> {
        val optionModelList = mutableListOf<OptionModel>()
        val cal = Calendar.getInstance()
        val ampm = if (cal.get(Calendar.AM_PM) == 0) {
            application.getString(com.chanho.common.R.string.common_am)
        } else {
            application.getString(com.chanho.common.R.string.common_pm)
        }
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)

        stringList.forEach {
            when (timeType) {
                TimeType.AMPM -> {
                    optionModelList.add(AmpmOptionModel(content = it, isClick = it == ampm))
                }
                TimeType.HOUR -> {
                    optionModelList.add(
                        HourOptionModel(
                            content = it, isClick = if (minute > 30) {
                                it == ((hour + 1) % 12).toString()
                            } else {
                                it == hour.toString()
                            }
                        )
                    )
                }
                TimeType.MINUTE -> {
                    optionModelList.add(
                        MinuteOptionModel(
                            content = it, isClick = if (minute > 30) {
                                it == "00"
                            } else {
                                it == "30"
                            }
                        )
                    )
                }
                else -> {}
            }

        }
        return optionModelList
    }

    fun onTimeSettingClick(type: TimeType) {
        _showDialogSetting.value = type

    }

    fun loadOptionModelList(type: TimeType) {
        _optionModelList.value = when (type) {
            TimeType.AMPM -> {
                _ampmOptionModelList
            }
            TimeType.HOUR -> {
                _hourOptionModelList
            }
            TimeType.MINUTE -> {
                _minuteOptionModelList
            }
            else -> {
                emptyList()
            }
        }.also { list ->
            list.withIndex().find { it.value.isClick }?.index?.let { position ->
                _isDialogInit.value = position
            }
        }
    }

    fun onDialogConfirmClick(timeType: TimeType) {
        _registerBtnStatus.value = true
        _optionModelList.value?.let { optionModelItem ->
            val selectedItem = optionModelItem.find { it.isClick }
            selectedItem?.let {
                when (timeType) {
                    TimeType.AMPM -> {
                        _ampmOptionModelList = _ampmOptionModelList.map {
                            it.copy(isClick = it.content == selectedItem.content)
                        }
                        _ampmResult.value = selectedItem.content
                    }
                    TimeType.HOUR -> {
                        _hourOptionModelList = _hourOptionModelList.map {
                            it.copy(isClick = it.content == selectedItem.content)
                        }
                        _hourResult.value = selectedItem.content
                    }
                    TimeType.MINUTE -> {
                        _minuteOptionModelList = _minuteOptionModelList.map {
                            it.copy(isClick = it.content == selectedItem.content)
                        }
                        _minuteResult.value = selectedItem.content
                    }
                    else -> {}
                }
            } ?: {

            }
        }
    }

    fun onDialogTimeItemClick(data: OptionModel, type: TimeType) {
        _optionModelList.value?.let { list ->
            val optionModelList = mutableListOf<OptionModel>()
            when (type) {
                TimeType.AMPM -> {
                    list.forEach {
                        if (it.content == data.content) {
                            optionModelList.add(AmpmOptionModel(it.content, true))
                        } else {
                            optionModelList.add(AmpmOptionModel(it.content, false))
                        }
                    }
                }
                TimeType.HOUR -> {
                    list.forEach {
                        if (it.content == data.content) {
                            optionModelList.add(HourOptionModel(it.content, true))
                        } else {
                            optionModelList.add(HourOptionModel(it.content, false))
                        }
                    }
                }
                TimeType.MINUTE -> {
                    list.forEach {
                        if (it.content == data.content) {
                            optionModelList.add(MinuteOptionModel(it.content, true))
                        } else {
                            optionModelList.add(MinuteOptionModel(it.content, false))
                        }
                    }
                }
                else -> {}
            }
            _optionModelList.value = optionModelList
        }
    }

    private fun addAlarmTime(time: String) {
        val convertToAmpmTime = Util.dateFormate_time_type.parse(time)
        val convertTo24Time = Util.dateFormate_24_hour.format(convertToAmpmTime).split(":")
        val hour = convertTo24Time[0].toInt()
        val minute = convertTo24Time[1].toInt()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,hour)
        cal.set(Calendar.MINUTE,minute)
        cal.set(Calendar.SECOND,0)
        val resultTime = Util.dateFormatForTime.format(cal.time)
        Log.e("calendar",resultTime)
        _registerBtnStatus.value =!alarmDao.isExistTime(resultTime)
        if(!alarmDao.isExistTime(resultTime)){
           val alarmCode =  alarmDao.getAll().takeIf { !it.isNullOrEmpty() }?.let {
                it.last().alarmCode+1
            }?:1
            alarmDao.insertAll(AlarmEntity(
                alarmCode = alarmCode,
                alarmTime = resultTime,
                alarmContent = "컨텐츠!"
            ))
            _onRegistrationClick.value = resultTime
        }
    }
}




