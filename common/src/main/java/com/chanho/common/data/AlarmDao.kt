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
    fun loadByAlarmCode(alarmCode:Int):AlarmEntity

    @Update
    fun updateAlarm(alarmEntity: AlarmEntity)

    @Insert
    fun insertAll(vararg alarmEntity: AlarmEntity)

    @Delete
    fun delete(alarmEntity: AlarmEntity)
}