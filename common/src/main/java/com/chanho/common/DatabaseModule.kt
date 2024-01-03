package com.chanho.common

import android.content.Context
import androidx.room.Room
import com.chanho.common.data.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object DatabaseModule {

    @Provides
    fun provideAlarmDatabase(
        @ActivityContext context:Context
    ): AlarmDatabase {
        return Room.databaseBuilder(
            context,
            AlarmDatabase::class.java,
            "alarm_database"
        ).allowMainThreadQueries().build()
    }
}