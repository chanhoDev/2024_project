package com.chanho.common

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chanho.common.data.AlarmDao
import com.chanho.common.data.AlarmDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAlarmDatabase(
        application:Application
    ): AlarmDatabase {
        return Room.databaseBuilder(
            application,
            AlarmDatabase::class.java,
            "alarm_database"
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(alarmDatabase: AlarmDatabase):AlarmDao{
        return alarmDatabase.AlarmDao()
    }
}