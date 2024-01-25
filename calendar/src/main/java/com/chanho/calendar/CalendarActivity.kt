package com.chanho.calendar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chanho.calendar.databinding.CalendarDayLayoutBinding
import com.chanho.calendar.databinding.Example3CalendarDayBinding
import com.chanho.calendar.databinding.Example3CalendarHeaderBinding
import com.chanho.calendar.databinding.Example3EventItemViewBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: CalendarDayLayoutBinding
    private lateinit var _viewModel: CalendarViewModel
    private var currentMonth = YearMonth.now()
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()
    private val colorList = mutableListOf<Int>(
        R.color.example_1_bg,
        R.color.example_2_black,
        R.color.example_2_red,
        R.color.example_3_blue,
        R.color.example_4_green
    )

    private val _observeError: (item: String) -> Unit = {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(this@CalendarActivity)
            .setMessage(R.string.example_3_dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private val inputDialog by lazy<AlertDialog> {
        val editText = AppCompatEditText(this)
        val layout = FrameLayout(this).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, this@CalendarActivity)
            setPadding(padding, padding, padding, padding)
            addView(
                editText, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
        AlertDialog.Builder(this@CalendarActivity)
            .setTitle(getString(R.string.example_3_input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")

            }
            .setNegativeButton(R.string.close, null)
            .create()
//            .apply {
//                this.setOnShowListener {
//                    // Show the keyboard
//                    editText.requestFocus()
//                    inputMethodManager
//                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//                }
//                setOnDismissListener {
//                    // Hide the keyboard
//                    inputMethodManager
//                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//                }
//            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalendarDayLayoutBinding.inflate(layoutInflater)
        _viewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
        setContentView(binding.root)
        onObserve()
        onBindView(savedInstanceState)
    }

    private fun onBindView(savedInstanceState: Bundle?) {
        with(binding) {
            exThreeRv.apply {
                layoutManager =
                    LinearLayoutManager(this@CalendarActivity, RecyclerView.VERTICAL, false)
                adapter = eventsAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        this@CalendarActivity,
                        RecyclerView.VERTICAL
                    )
                )
            }

            exThreeCalendar.monthScrollListener = {
                titleTextview.text = if (it.yearMonth.year == today.year) {
                    titleSameYearFormatter.format(it.yearMonth)
                } else {
                    titleFormatter.format(it.yearMonth)
                }
                // Select the first day of the visible month.
                selectDate(it.yearMonth.atDay(1))
            }
            prevBtn.setOnClickListener {
                currentMonth = currentMonth.plusMonths(-1)
                exThreeCalendar.scrollToMonth(currentMonth)
            }
            forwardBtn.setOnClickListener {
                currentMonth = currentMonth.plusMonths(1)
                exThreeCalendar.scrollToMonth(currentMonth)
            }

            val daysOfWeek = daysOfWeek()
//            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(50)
            val endMonth = currentMonth.plusMonths(50)
            configureBinders(daysOfWeek)
            exThreeCalendar.apply {
                setup(startMonth, endMonth, daysOfWeek.first())

                scrollToMonth(currentMonth)
            }

            if (savedInstanceState == null) {
                // Show today's events initially.
                exThreeCalendar.post { selectDate(today) }
            }
            exThreeAddButton.setOnClickListener { inputDialog.show() }

        }
    }

    private fun onObserve() {
        with(_viewModel) {
//            deleteAlarmItem.observe(viewLifecycleOwner, _observeDeleteAlarmItem)
        }
    }

    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var height = 0
        Log.e("touchEvent","${event.toString()}")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_MOVE -> {
                height = binding.main.height
                val guideLine = binding.guideline
                val params = guideLine.layoutParams as ConstraintLayout.LayoutParams
                var result = (event.rawY) / height
                if(result >0.06 && result<=1){
                    params.guidePercent =result
                    guideLine.layoutParams = params
                    binding.exThreeCalendar.notifyCalendarChanged()
                }

            }

            else -> {
                return false
            }
        }
        return true
    }


    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(this@CalendarActivity.events[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
        Log.e("updateAdapterForDate", "$date :: $selectedDate")
        binding.exThreeCalendar.notifyCalendarChanged()
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(
                this@CalendarActivity,
                R.string.example_3_empty_input_text,
                Toast.LENGTH_LONG
            )
                .show()
        } else {
            selectedDate?.let {
                events[it] =
                    events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }


    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = Example3CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.exThreeCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                Log.e("calendar", "$data ")
                container.day = data
                val layout = container.binding.exThreeDayLayout
                val textView = container.binding.exThreeDayText
                val dotLayout = container.binding.dotLayout
//                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    dotLayout.removeAllViews()
                    val eventList = events[data.date]
                    eventList?.forEachIndexed {index,event ->
                        dotLayout.addView(createBtn(binding.root.context,index,event.text))
                    }
                    when (data.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundColor(resources.getColor(R.color.example_3_blue,null))
                        }

                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundColor(resources.getColor(R.color.example_3_blue_light,null))
                        }

                        else -> {
                            textView.setTextColorRes(R.color.example_3_black)
                            textView.background =null
                        }
                    }
                } else {
                    textView.makeInVisible()
//                    dotView.makeInVisible()
                }
            }
            fun createBtn(context: Context,index:Int,content:String): View {
                val textView = TextView(context)
                val dp = context.resources.displayMetrics.density+0.5f
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                     setMargins(5,5,5,0)
                }
                textView.apply {
                    text = content
                    setTextColor(resources.getColor(R.color.white,null))
                    textSize = 2*dp
                    layoutParams = lp
                    setBackgroundColor(resources.getColor(colorList[index%(colorList.size-1)],null))
//                    setBackgroundResource(R.drawable.example_3_today_bg)
                    id = ViewCompat.generateViewId()
                }
                return textView
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.exThreeCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.first().toString()
                                tv.setTextColorRes(R.color.example_3_black)
                            }
                    }
                }
            }
    }
}


data class Event(val id: String, val text: String, val date: LocalDate)

class Example3EventsAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
        return Example3EventsViewHolder(
            Example3EventItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        )
    }

    override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class Example3EventsViewHolder(private val binding: Example3EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(events[bindingAdapterPosition])
            }
        }

        fun bind(event: Event) {
            binding.itemEventText.text = event.text
        }
    }
}

