package com.chanho.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chanho.common.SingleLiveEvent

class MedicationAlarmViewModel constructor(
    private val application: Application
) : AndroidViewModel(application) {
    private val _medicationList = MutableLiveData<List<MedicationAlarmTimeModel>>()
    val medicationList: LiveData<List<MedicationAlarmTimeModel>> = _medicationList

    private val _deleteAlarmItem = SingleLiveEvent<MedicationAlarmTimeModel>()
    val deleteAlarmItem = _deleteAlarmItem

    init {
//        _userInfo.value =
//            Gson().fromJson(PrefHelper[PrefHelper.KEY_USER_INFO, ""], UserInfo::class.java)
    }

    fun callAlarmList() {
        _medicationList.value = mutableListOf(
            MedicationAlarmTimeModel(1,10,20),
            MedicationAlarmTimeModel(2,10,22),
            MedicationAlarmTimeModel(3,13,22),
            MedicationAlarmTimeModel(4,15,22)
        )
//        viewModelScope.launch {
//            _medicationAlarmUseCase.getMedicationList(
//                _userInfo.value!!.memberNo
//            ).collect { response ->
//                when (response) {
//                    is ApiResult.Empty -> {
//                        _medicationList.value = emptyList()
//                    }
//                    is ApiResult.Fail.Error -> {
//                       setError(response.toString())
//                    }
//                    is ApiResult.Fail.Exception -> {
//                        setError(response.toString())
//                    }
//                    is ApiResult.Loading -> {}
//                    is ApiResult.Success -> {
//                        if(response.data.header.isSuccessful){
//                            response.data.medicineScheduleList?.let { item ->
//                                _medicationList.value = item
//                            } ?: run {
//                                _medicationList.value = emptyList()
//                            }
//                        }else{
//                            setError(response.toString())
//                        }
//                    }
//                }
//            }
//        }
    }

    fun removeAlarmItem(item: MedicationAlarmTimeModel) {
        _medicationList.value = _medicationList.value?.filter { it.medicineScheduleSeq !=item.medicineScheduleSeq }
//        viewModelScope.launch {
//            _medicationAlarmUseCase.deleteMedication(
//                userId = _userInfo.value!!.memberNo, medicineScheduleSeq = item.medicineScheduleSeq
//            ).collect { response ->
//                Log.e("removeAlarmItem ", response.toString())
//                when (response) {
//                    is ApiResult.Empty -> {}
//                    is ApiResult.Fail.Error -> {
//                        setError(response.toString())
//                    }
//                    is ApiResult.Fail.Exception -> {
//                        setError(response.toString())
//                    }
//                    is ApiResult.Loading -> {}
//                    is ApiResult.Success -> {
//                        if(response.data.header.isSuccessful){
//                            callAlarmList()
//                            _deleteAlarmItem.value = item
//                        }else{
//                            setError(response.toString())
//                        }
//
//                    }
//                }
//            }
//        }
    }
}