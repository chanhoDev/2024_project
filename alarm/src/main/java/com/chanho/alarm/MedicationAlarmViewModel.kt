package com.chanho.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chanho.common.SingleLiveEvent
import com.chanho.common.data.AlarmDao
import com.chanho.common.data.AlarmDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationAlarmViewModel @Inject constructor(
    private val application: Application,
    private val alarmDao: AlarmDao
) : AndroidViewModel(application) {
    private val _medicationList = MutableLiveData<List<AlarmTimeModel>>()
    val medicationList: LiveData<List<AlarmTimeModel>> = _medicationList

    private val _deleteAlarmItem = SingleLiveEvent<AlarmTimeModel>()
    val deleteAlarmItem = _deleteAlarmItem


    fun callAlarmList() {
        viewModelScope.launch {
            val alarmList = alarmDao.getAll()
            alarmList?.sortedBy { it.alarmTime }?.map {
                AlarmTimeModel(
                    alarmCode = it.alarmCode,
                    alarmTime = it.alarmTime,
                    alarmContent = it.alarmContent
                )
            }?.let { alarmTimeModelList ->
                _medicationList.value = alarmTimeModelList
            }
        }
    }

    fun removeAlarmItem(alarmTimeModel: AlarmTimeModel) {
        val deleteResult = alarmDao.delete(alarmTimeModel.alarmTime)
        if(deleteResult==1){
            _deleteAlarmItem.value = alarmTimeModel
            callAlarmList()
        }
    }
}