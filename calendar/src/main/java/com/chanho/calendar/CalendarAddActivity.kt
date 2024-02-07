package com.chanho.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Switch
import android.widget.TimePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.chanho.calendar.databinding.ActivityCalendarAddBinding
import com.chanho.calendar.databinding.ItemDayBinding
import com.chanho.common.RoundedBottomSheetDialogFragment
import com.chanho.common.Util
import com.chanho.common.databinding.LayoutInadvanceSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@AndroidEntryPoint
class CalendarAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarAddBinding
    private lateinit var viewModel: CalendarAddViewModel
    private var linearLayoutMananger: LinearLayoutManager? = null
    private val _inadavanceAdapter :InadavanceAdapter by lazy {
        InadavanceAdapter(object :InadvanceItemListener{
            override fun onInAdvanceItemClick(inadvancedModel: InadvanceModel) {
                Log.e("onInadavanceItemClick",inadvancedModel.toString())
                viewModel.onInadvanceClick(inadvancedModel)
            }
        })
    }
    private val _repeatAdapter: RepeatAdapter by lazy {
        RepeatAdapter(object : RepeatItemListener {
            //TODO 반복요일 선택
            override fun onRepeatDayModelItemClick(repeatDayModel: RepeatDayModel) {
                viewModel.onRepeatItemClicked(repeatDayModel)
            }
        })
    }
    private val _observeRepeatItemList = Observer<List<RepeatDayModel>> {
        _repeatAdapter.submitList(it)
    }

    private val _observeSaveBtnStatus = Observer<Boolean> {
        Log.e("btnStatus", it.toString())
    }

    private val _observeCalednarBtnStatus = Observer<Boolean> {
        Log.e("calendarBtnStatus", it.toString())
//        calendarBtn?.isClickable = it
    }

    private val _observeDisplayCalendarData = Observer<String> {
        Log.e("displayCalendarData", it)
        binding.memoSelectTime.text = it
    }

    private val _observeInadvanceList = Observer<List<InadvanceModel>>{
        _inadavanceAdapter.submitList(it)
    }

    private val _observeDisplayInadvanceData = Observer<String> {
        Log.e("DisplayInadvanceData", it)
        binding.memoInadvanceAlarmText.text = it
    }


    private val _observeCalendarData = Observer<CalendarData> {
        Log.e("CalendarData", it.toString())
        val intent = Intent(this@CalendarAddActivity, CalendarActivity::class.java)
        intent.putExtra(CalendarActivity.CALENDAR_DATA, it)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarAddBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CalendarAddViewModel::class.java]
        viewModel.onLoadData()
        setContentView(binding.root)
        onBindView()
        onObserve()
    }

    private fun onBindView() {
        with(binding) {
            //TODO 뒤로가기
            memoArrowBack.setOnClickListener {
                finish()
            }
            //TODO 저장이벤트
            memoSaveText.setOnClickListener {
                viewModel.onSaveCalendar()
            }
            //TODO 텍스트 입력
            memoEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    viewModel.calendarData.value?.let {
                        viewModel.saveCalendarData(it.copy(content = p0.toString()))
                        viewModel.checkBtnStatus()
                    }
                }
            })
            //TODO 알림시간 입력
            memoSelectTime.setOnClickListener {
                RoundedBottomSheetDialogFragment(
                    object : RoundedBottomSheetDialogFragment.OnClickListener {
                        var timePicker: TimePicker? = null
                        val calendarDate = Calendar.getInstance()
                        override fun onPositiveClick() {
                            Log.e(
                                "click_timePicker",
                                "${timePicker?.hour} ${timePicker?.minute}"
                            )
                            viewModel.calendarData.value?.let {
                                viewModel.saveCalendarData(
                                    it.copy(
                                        date = Util.simpleDateFormat.format(calendarDate.time),
                                        time = "${timePicker?.hour}:${timePicker?.minute}:00"
                                    )
                                )
                            }

                        }

                        override fun onNegativeClick() {
                        }

                        override fun onBindContent(view: View) {
                            var calendar: CalendarView? =
                                view.findViewById(com.chanho.common.R.id.calendar_view) as CalendarView
                            timePicker =
                                view.findViewById(com.chanho.common.R.id.time_picker_view) as TimePicker
                            timePicker?.setIs24HourView(true)
                            viewModel.calendarData.value?.let {
                                val cal = Calendar.getInstance()
                                calendar?.date = if (it.date.isNullOrEmpty()) {
                                    cal.timeInMillis
                                } else {
                                    cal.time = Util.simpleDateFormat.parse(it.date)
                                    cal.timeInMillis
                                }
                                if (it.time.isNotEmpty()) {
                                    val tempTime = it.time.split(":")
                                    timePicker?.hour = tempTime[0].toInt()
                                    timePicker?.minute = tempTime[1].toInt()
                                    Log.e("timePicker", "$tempTime")
                                }
                            }

                            calendar?.setOnDateChangeListener { calendarView, year, month, date ->
                                calendarDate.set(Calendar.YEAR, year)
                                calendarDate.set(Calendar.MONTH, month)
                                calendarDate.set(Calendar.DATE, date)
                            }

                            Log.e(
                                "timePicker",
                                "${timePicker?.hour} ${timePicker?.minute}"
                            )
                        }

                        override fun onCancel() {
                        }

                        override fun onDismiss() {
                        }

                    },
                    title = "알림시간 입력",
                    contentResource = com.chanho.common.R.layout.layout_time_select,
                    positive = "확인하기"
                ).show(this@CalendarAddActivity.supportFragmentManager, null)
            }
            repeatRecyclerview.adapter = _repeatAdapter
            //TODO 미리알림 선택
            memoInadvanceAlarmLayout.setOnClickListener {
                RoundedBottomSheetDialogFragment(
                    object : RoundedBottomSheetDialogFragment.OnClickListener {
                        var timePicker: TimePicker? = null
                        override fun onPositiveClick() {
                            Log.e(
                                "click_inadvance",
                                ""
                            )
                            viewModel.calendarData.value?.let {
                                viewModel.saveInadvance()
                            }
                        }

                        override fun onNegativeClick() {
                        }

                        override fun onBindContent(view: View) {
                            var recyclerview: RecyclerView =
                                view.findViewById(com.chanho.common.R.id.layout_recycler_view) as RecyclerView
                            recyclerview.layoutManager = LinearLayoutManager(this@CalendarAddActivity,RecyclerView.VERTICAL,false)
                            recyclerview.adapter = _inadavanceAdapter
                            Log.e(
                                "timePicker",
                                "${timePicker?.hour} ${timePicker?.minute}"
                            )
                            viewModel.setInadavanceList()
                        }

                        override fun onCancel() {
                        }

                        override fun onDismiss() {
                        }

                    },
                    title = "미리알림 선택",
                    contentResource = com.chanho.common.R.layout.layout_recyclerview,
                    positive = "확인하기"
                ).show(this@CalendarAddActivity.supportFragmentManager, null)
            }
            //TODO 스티커 선택
            memoStickerLayout.setOnClickListener {

            }
            //TODO 알림 레이아웃 클릭이벤트 켜기 끄기
            memoAlarmOnOffSwitchLayout.setOnClickListener {
                memoAlarmOnOffSwitch.performClick()
            }
            //TODO 알림 클릭이벤트 켜기 끄기
            memoAlarmOnOffSwitch.setOnClickListener {
                val switch = it as Switch
                viewModel.calendarData.value?.let {
                    Log.e("switch_status", switch.isChecked.toString())
                    viewModel.calendarData.value = it.copy(alarmOnAndOff = switch.isChecked)
                }
            }
        }
    }

    private fun onObserve() {
        with(viewModel) {
            saveBtnStatus.observe(this@CalendarAddActivity, _observeSaveBtnStatus)
            repeatDayModelList.observe(this@CalendarAddActivity, _observeRepeatItemList)
            calendarBtnStatus.observe(this@CalendarAddActivity, _observeCalednarBtnStatus)
            displayCalendarData.observe(this@CalendarAddActivity, _observeDisplayCalendarData)
            inadavanceList.observe(this@CalendarAddActivity,_observeInadvanceList)
            displayInadvanceData.observe(this@CalendarAddActivity,_observeDisplayInadvanceData)
            resultCalendarData.observe(this@CalendarAddActivity,_observeCalendarData)
        }
    }
}

@Parcelize
data class RepeatDayModel(
    val dayOfWeek: String,
    val isClick: Boolean
):Parcelable

class RepeatAdapter(
    private val listener: RepeatItemListener
) : androidx.recyclerview.widget.ListAdapter<RepeatDayModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<RepeatDayModel>() {
        override fun areItemsTheSame(
            oldItem: RepeatDayModel,
            newItem: RepeatDayModel
        ): Boolean {
            return oldItem.dayOfWeek == newItem.dayOfWeek
        }

        override fun areContentsTheSame(
            oldItem: RepeatDayModel,
            newItem: RepeatDayModel
        ): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MedicationAlarmItemViewHolder(
            ItemDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) as RepeatDayModel
        val medicationAlarmItemViewHolder = (holder as MedicationAlarmItemViewHolder)
        with(medicationAlarmItemViewHolder) {
            itemText.text = item.dayOfWeek
            if (item.isClick) {
                itemText.setBackgroundColor(
                    this.itemView.resources.getColor(
                        R.color.example_3_blue_light,
                        null
                    )
                )
            } else {
                itemText.setBackgroundColor(this.itemView.resources.getColor(R.color.white, null))
            }
            this.item = item
        }
    }
}

class MedicationAlarmItemViewHolder(
    binding: ItemDayBinding,
    listener: RepeatItemListener
) : RecyclerView.ViewHolder(binding.root) {
    val itemText = binding.itemText
    var item: RepeatDayModel? = null

    init {
        itemText.setOnClickListener {
            item?.let { item ->
                listener.onRepeatDayModelItemClick(item)
            }
        }
    }
}

interface RepeatItemListener {
    fun onRepeatDayModelItemClick(repeatDayModel: RepeatDayModel)
}


///
enum class InadvanceType(val typeName: String) {
    NOTING("없음"), ONE_MINUTE("1분뒤"), TWO_MINUTE("2분뒤"), FIVE_MINUTE("5분뒤"), TIRTY_MINUTE("30분뒤");

}

data class InadvanceModel(
    val inadavanceType: InadvanceType,
    val isClick: Boolean
)

class InadavanceAdapter(
    private val listener: InadvanceItemListener
) : androidx.recyclerview.widget.ListAdapter<InadvanceModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<InadvanceModel>() {
        override fun areItemsTheSame(
            oldItem: InadvanceModel,
            newItem: InadvanceModel
        ): Boolean {
            return oldItem.inadavanceType == newItem.inadavanceType
        }

        override fun areContentsTheSame(
            oldItem: InadvanceModel,
            newItem: InadvanceModel
        ): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InadvanceItemViewHolder(
            LayoutInadvanceSelectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) as InadvanceModel
        val inadvanceItemViewHolder = (holder as InadvanceItemViewHolder)
        with(inadvanceItemViewHolder) {
            itemText.text = item.inadavanceType.typeName
            if (item.isClick) {
                itemText.setBackgroundColor(
                    this.itemView.resources.getColor(
                        R.color.example_3_blue_light,
                        null
                    )
                )
            } else {
                itemText.setBackgroundColor(this.itemView.resources.getColor(R.color.white, null))
            }
            this.item = item
        }
    }
}

class InadvanceItemViewHolder(
    binding: LayoutInadvanceSelectBinding,
    listener: InadvanceItemListener
) : RecyclerView.ViewHolder(binding.root) {
    val itemText = binding.itemText
    var item: InadvanceModel? = null

    init {
        itemText.setOnClickListener {
            item?.let { item ->
                listener.onInAdvanceItemClick(item)
            }
        }
    }
}

interface InadvanceItemListener {
    fun onInAdvanceItemClick(inadvancedModel: InadvanceModel)
}

