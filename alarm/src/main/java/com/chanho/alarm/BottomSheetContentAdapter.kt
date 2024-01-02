package com.nhn.waplat.presentation.medication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chanho.alarm.AmpmOptionModel
import com.chanho.alarm.BirthDayOptionModel
import com.chanho.alarm.BirthMonthOptionModel
import com.chanho.alarm.BirthYearOptionModel
import com.chanho.alarm.EndDayOptionModel
import com.chanho.alarm.EndMonthOptionModel
import com.chanho.alarm.EndYearOptionModel
import com.chanho.alarm.HourOptionModel
import com.chanho.alarm.MinuteOptionModel
import com.chanho.alarm.OptionModel
import com.chanho.alarm.R
import com.chanho.alarm.StartDayOptionModel
import com.chanho.alarm.StartMonthOptionModel
import com.chanho.alarm.StartYearOptionModel
import com.chanho.alarm.databinding.ItemOptionBinding


class BottomSheetContentAdapter constructor(
    private val listener: CommonContentListener
) : ListAdapter<OptionModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<OptionModel>() {
        override fun areItemsTheSame(
            oldItem: OptionModel,
            newItem: OptionModel
        ): Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(
            oldItem: OptionModel,
            newItem: OptionModel
        ): Boolean {
            return oldItem.content == newItem.content && oldItem.isClick == newItem.isClick
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BottomSheeetContentViewHolder(
            ItemOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val scheduleItemViewHolder = (holder as BottomSheeetContentViewHolder)
        val item = getItem(position)
        scheduleItemViewHolder.item = item
        with(scheduleItemViewHolder) {
            itemOptionText.text = when (item) {
                is AmpmOptionModel -> {
                    item.content
                }
                is HourOptionModel -> {
                    itemView.resources.getString(com.chanho.common.R.string.bottom_dialog_hour, item.content)
                }
                is MinuteOptionModel -> {
                    itemView.resources.getString(com.chanho.common.R.string.bottom_dialog_minute, item.content)
                }
                is BirthYearOptionModel,
                is StartYearOptionModel,
                is EndYearOptionModel -> {
                    itemView.resources.getString(com.chanho.common.R.string.bottom_dialog_year, item.content)
                }
                is BirthMonthOptionModel,
                is StartMonthOptionModel,
                is EndMonthOptionModel -> {
                    itemView.resources.getString(com.chanho.common.R.string.bottom_dialog_month, item.content)
                }
                is BirthDayOptionModel,
                is StartDayOptionModel,
                is EndDayOptionModel -> {
                    itemView.resources.getString(com.chanho.common.R.string.bottom_dialog_day, item.content)
                }
                else -> {
                    item.content
                }
            }
            itemOptionText.isSelected = item.isClick
        }
    }

}


class BottomSheeetContentViewHolder(
    binding: ItemOptionBinding,
    listener: CommonContentListener,
) : RecyclerView.ViewHolder(binding.root) {
    val itemOptionText = binding.itemOptionText
    var item: OptionModel? = null

    init {
        itemOptionText.setOnClickListener {
            item?.let {
                listener.onItemClick(data = it)
            }
        }
    }
}

interface CommonContentListener {
    fun onItemClick(data: OptionModel)
}