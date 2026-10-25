package com.agrovision.ai.di

import android.content.Context
import androidx.room.Room
import com.agrovision.ai.data.local.dao.ScanDao
import com.agrovision.ai.data.local.database.AgroVisionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAgroVisionDatabase(
        @ApplicationContext context: Context
    ): AgroVisionDatabase {
        return Room.databaseBuilder(
            context,
            AgroVisionDatabase::class.java,
            AgroVisionDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideScanDao(database: AgroVisionDatabase): ScanDao {
        return database.scanDao()
    }
}
