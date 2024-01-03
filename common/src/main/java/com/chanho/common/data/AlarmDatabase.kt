package com.chanho.common.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
abstract class AlarmDatabase :RoomDatabase(){
    abstract fun AlarmDao():AlarmDao
}