package com.chanho.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chanho.alarm.databinding.ItemMedicationAlarmBinding
import com.chanho.common.Util.dateFormate_24_hour
import com.chanho.common.Util.dateFormate_time_type


class AlarmAdapter(
    private val listener: AlarmItemListener
) : ListAdapter<AlarmTimeModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<AlarmTimeModel>() {
        override fun areItemsTheSame(
            oldItem: AlarmTimeModel,
            newItem: AlarmTimeModel
        ): Boolean {
            return oldItem.alarmCode == newItem.alarmCode
        }

        override fun areContentsTheSame(
            oldItem: AlarmTimeModel,
            newItem: AlarmTimeModel
        ): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MedicationAlarmItemViewHolder(
            ItemMedicationAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) as AlarmTimeModel
        val medicationAlarmItemViewHolder = (holder as MedicationAlarmItemViewHolder)
        medicationAlarmItemViewHolder.item = item
        medicationAlarmItemViewHolder.timeContent.text =item.alarmContent
        medicationAlarmItemViewHolder.timeText.text =item.alarmTime
    }
}

class MedicationAlarmItemViewHolder(
    binding: ItemMedicationAlarmBinding,
    listener: AlarmItemListener
) : RecyclerView.ViewHolder(binding.root) {
    private val timeDeleteBtn = binding.timeDeleteBtn
    val timeContent = binding.timeContent
    val timeText = binding.timeText
    var item: AlarmTimeModel? = null

    init {
        timeDeleteBtn.setOnClickListener {
            item?.let { item ->
                listener.onAlarmItemClick(item)
            }
        }
    }
}

interface AlarmItemListener {
    fun onAlarmItemClick(alarmTimeModel: AlarmTimeModel)
}
