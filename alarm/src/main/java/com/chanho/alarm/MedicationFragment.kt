package com.chanho.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chanho.alarm.databinding.FragmentMedicationBinding
import com.chanho.common.AlarmFunctions.callAlarm
import com.chanho.common.AlarmFunctions.cancelAlarm
import com.chanho.common.Constants.REQUEST_ALARM_TIME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationFragment : Fragment() {
    private lateinit var _binding: FragmentMedicationBinding
    private lateinit var _viewModel: MedicationAlarmViewModel
    private val _adapter: AlarmAdapter by lazy {
        AlarmAdapter(object : AlarmItemListener {
            override fun onAlarmItemClick(alarmTimeModel: AlarmTimeModel) {
                _viewModel.removeAlarmItem(alarmTimeModel)
            }
        })
    }
    private val _observeMedicationList: (item: List<AlarmTimeModel>) -> Unit = {
        if (it.isNullOrEmpty()) {
            _binding.medicationEmptyLayout.visibility = View.VISIBLE
        } else {
            _binding.medicationEmptyLayout.visibility = View.GONE
            it.forEach { medicationItem ->
//                val time =
//                    getString(
//                        R.string.alarm_time_format,
//                        medicationItem.alarmTime
//                    )
//                callAlarm(
//                    requireContext(),
//                    alarmPopupType = Constants.AlarmPopupType.MEDICATION,
//                    time = time,
//                    content = getString(com.chanho.common.R.string.medication_alarm_title),
//                    alarmCode = medicationItem.medicineScheduleSeq
//                )
            }
        }
        _adapter.submitList(it)
    }
    private val _observeDeleteAlarmItem: (item: AlarmTimeModel) -> Unit = {
        Toast.makeText(
            requireContext(),
            getString(R.string.alarm_time_delete),
            Toast.LENGTH_SHORT
        ).show()

//        val time =
//            getString(
//                R.string.alarm_time_format,
//                medicationItem.alarmTime
//            )
//        cancelAlarm(
//            context = requireContext(),
//            alarmPopupType = Constants.AlarmPopupType.MEDICATION,
//            time = time,
//            alarmCode = it.medicineScheduleSeq
//        )
    }

    private val _observeError: (item: String) -> Unit = {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewModel = ViewModelProvider(this)[MedicationAlarmViewModel::class.java]
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO 권한요청 띄우면 됨

        setFragmentResultListener()
        onObserve()
        _viewModel.callAlarmList()
        with(_binding) {
            registrationBtn.setOnClickListener {
                findNavController().navigate(R.id.action_MedicationFragment_to_MedicationRegisterFragment)
            }
            recyclerView.apply {
                itemAnimator = null
                adapter = _adapter
            }
        }
    }

    private fun onObserve() {
        with(_viewModel) {
            medicationList.observe(viewLifecycleOwner, _observeMedicationList)
            deleteAlarmItem.observe(viewLifecycleOwner, _observeDeleteAlarmItem)
        }
    }

    private fun setFragmentResultListener() {
        setFragmentResultListener(REQUEST_ALARM_TIME) { type, bundle ->
            _viewModel.callAlarmList()
        }
    }
}