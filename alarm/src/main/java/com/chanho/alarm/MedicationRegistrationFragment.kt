package com.chanho.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chanho.alarm.databinding.FragmentMedicationRegistrationBinding
import com.chanho.common.Constants.REQUEST_ALARM_TIME
import com.chanho.common.Util.IS_DEVELOPER_MODE
import com.chanho.common.Util.smoothScrollToPositionWithDelay
import com.nhn.waplat.presentation.medication.BottomSheetContentAdapter
import com.nhn.waplat.presentation.medication.CommonContentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentMedicationRegistrationBinding
    private lateinit var viewModel: MedicationRegistrationViewModel
    private var _adapter: BottomSheetContentAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val _observeRegisterBtnStatus = Observer<Boolean> {
        binding.registrationBtn.isEnabled = it
        binding.registrationBtn.text = if (it) {
            getText(R.string.alarm_register)
        } else {
            getText(R.string.alarm_already_register)
        }

        binding.registrationBtn.setTextColor(
            if (it) {
                resources.getColor(com.chanho.common.R.color.bg_00, null)
            } else {
                resources.getColor(com.chanho.common.R.color.grey_500, null)
            }
        )
    }
    private val _observeIsDialogInit = Observer<Int> { itemPosition ->
        linearLayoutManager?.smoothScrollToPositionWithDelay(
            position = itemPosition, this@MedicationRegistrationFragment.requireContext()
        )
    }
    private val _observeOnRegistrationClick: (item: String) -> Unit = {
        setFragmentResult(REQUEST_ALARM_TIME, bundleOf())
        findNavController().navigateUp()
    }
    private val _observeErrorMessage: (item: String) -> Unit = {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }
    private val _observeAmpmResult: (item: String) -> Unit = {
        binding.ampmText.text = it
    }
    private val _observeHourResult: (item: String) -> Unit = {
        binding.hourText.text = resources.getString(R.string.alarm_hour, it)
    }
    private val _observeMinuteResult: (item: String) -> Unit = {
        binding.minuteText.text = resources.getString(R.string.alarm_minute, it)
    }
    private val _observeOptionModelItemClick: (item: List<OptionModel>) -> Unit = {
        _adapter?.submitList(it)
    }
    private val _observeOptionModeList: (item: TimeType) -> Unit = { timeType ->
        RoundedBottomSheetDialogFragment(
            object : RoundedBottomSheetDialogFragment.OnClickListener {
                override fun onPositiveClick() {
                    viewModel.onDialogConfirmClick(timeType)
                }

                override fun onNegativeClick() {}

                override fun onBindContent(view: View) {
                    val recyclerView =
                        view.findViewById<RecyclerView>(com.chanho.common.R.id.layout_recycler_view)
                    when (timeType) {
                        TimeType.AMPM -> {
                            val layoutMananger = GridLayoutManager(requireContext(), 2)
                            recyclerView.layoutManager = layoutMananger
                        }

                        TimeType.HOUR -> {
                            linearLayoutManager = LinearLayoutManager(requireContext())
                            linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
                            recyclerView.layoutManager = linearLayoutManager
                        }

                        TimeType.MINUTE -> {
                            linearLayoutManager = LinearLayoutManager(requireContext())
                            linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
                            recyclerView.layoutManager = linearLayoutManager
                        }

                        else -> {}
                    }
                    _adapter = BottomSheetContentAdapter(object : CommonContentListener {
                        override fun onItemClick(data: OptionModel) {
                            viewModel.onDialogTimeItemClick(
                                data = data, type = timeType
                            )
                        }
                    })
                    recyclerView.adapter = _adapter
                    viewModel.loadOptionModelList(timeType)
                }

                override fun onCancel() {
                }

                override fun onDismiss() {
                }
            },
            title = when (timeType) {
                TimeType.AMPM -> getString(R.string.alarm_dialog_title_ampm)
                TimeType.HOUR -> getString(R.string.alarm_dialog_title_hour)
                TimeType.MINUTE -> getString(R.string.alarm_dialog_title_minute)
                else -> {
                    null
                }
            },
            contentResource = com.chanho.common.R.layout.layout_recyclerview,
            positive = getString(com.chanho.common.R.string.common_select),
        ).show(parentFragmentManager, "")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MedicationRegistrationViewModel::class.java)
        binding = FragmentMedicationRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO 테스트용
        val minuteList = emptyArray<String>().toMutableList().apply {
            for (i in 0..59) {
                if (i < 10) {
                    add("0$i")
                } else {
                    add("$i")

                }
            }
        }



        viewModel.onLoadTimeDataList(
            ampmList = binding.root.resources.getStringArray(com.chanho.common.R.array.AMPM)
                .toList(),
            hourList = binding.root.resources.getStringArray(com.chanho.common.R.array.HOUR)
                .toList(),
            minuteList = minuteList.toList()
        )
        onObserve()

        with(binding) {
            ampmText.setOnClickListener {
                viewModel.onTimeSettingClick(TimeType.AMPM)
            }
            hourText.setOnClickListener {
                viewModel.onTimeSettingClick(TimeType.HOUR)
            }
            minuteText.setOnClickListener {
                viewModel.onTimeSettingClick(TimeType.MINUTE)
            }
            registrationBtn.setOnClickListener {
                viewModel.onRegistrationClick()
            }
        }
    }

    private fun onObserve() {
        with(viewModel) {
            showDialogSetting.observe(viewLifecycleOwner, _observeOptionModeList)
            optionModelList.observe(viewLifecycleOwner, _observeOptionModelItemClick)
            ampmResult.observe(viewLifecycleOwner, _observeAmpmResult)
            hourResult.observe(viewLifecycleOwner, _observeHourResult)
            minuteResult.observe(viewLifecycleOwner, _observeMinuteResult)
            onRegistrationClick.observe(viewLifecycleOwner, _observeOnRegistrationClick)
            errorMessage.observe(viewLifecycleOwner, _observeErrorMessage)
            registerBtnStatus.observe(viewLifecycleOwner, _observeRegisterBtnStatus)
            isDialogInit.observe(viewLifecycleOwner, _observeIsDialogInit)

        }
    }
}