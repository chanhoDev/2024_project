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
) : ListAdapter<MedicationAlarmTimeModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<MedicationAlarmTimeModel>() {
        override fun areItemsTheSame(
            oldItem: MedicationAlarmTimeModel,
            newItem: MedicationAlarmTimeModel
        ): Boolean {
            return oldItem.medicineScheduleSeq == newItem.medicineScheduleSeq
        }

        override fun areContentsTheSame(
            oldItem: MedicationAlarmTimeModel,
            newItem: MedicationAlarmTimeModel
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
        val item = getItem(position) as MedicationAlarmTimeModel
        val medicationAlarmItemViewHolder = (holder as MedicationAlarmItemViewHolder)
        val hour = item.hour
        val minute = item.minute
        val time = "$hour:$minute"
        val convertTo24Time = dateFormate_24_hour.parse(time)
        val conver24toAmpmTime = dateFormate_time_type.format(convertTo24Time)
        medicationAlarmItemViewHolder.item = item
        medicationAlarmItemViewHolder.ampmText.text = conver24toAmpmTime.split(" ")[0]
        medicationAlarmItemViewHolder.timeText.text = conver24toAmpmTime.split(" ")[1]
    }
}

class MedicationAlarmItemViewHolder(
    binding: ItemMedicationAlarmBinding,
    listener: AlarmItemListener
) : RecyclerView.ViewHolder(binding.root) {
    private val timeDeleteBtn = binding.timeDeleteBtn
    val ampmText = binding.ampmText
    val timeText = binding.timeText
    var item: MedicationAlarmTimeModel? = null

    init {
        timeDeleteBtn.setOnClickListener {
            item?.let { item ->
                listener.onAlarmItemClick(item)
            }
        }
    }
}

interface AlarmItemListener {
    fun onAlarmItemClick(medicationAlarmTimeModel: MedicationAlarmTimeModel)
}
