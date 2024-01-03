package com.chanho.common.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chanho.common.Constants

@Entity
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    @ColumnInfo(name = "alarm_code")
    val alarmCode:Int,
    @ColumnInfo(name = "alarm_time")
    val alarmTime:String,
    @ColumnInfo(name = "alarm_content")
    val alarmContent:String
)
