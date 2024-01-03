package com.chanho.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
abstract class AlarmDatabase :RoomDatabase(){

    abstract fun AlarmDao():AlarmDao
}
