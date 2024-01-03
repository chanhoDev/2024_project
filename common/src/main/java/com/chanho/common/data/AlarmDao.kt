package com.chanho.common.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarmentity")
    fun getAll():List<AlarmEntity>?

    @Query("SELECT * FROM alarmentity WHERE alarm_code IN (:alarmCode) LIMIT 1")
    fun loadByAlarmCode(alarmCode:Int):AlarmEntity?

    @Query("SELECT EXISTS(SELECT * FROM alarmentity WHERE alarm_time IN (:alarmTime))")
    fun isExistTime(alarmTime:String):Boolean

    @Query("DELETE FROM alarmentity WHERE alarm_time IN(:alarmTime)")
    fun delete(alarmTime: String):Int

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)

    @Insert
    fun insertAll(vararg alarmEntity: AlarmEntity)

}