package com.agrovision.ai.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.agrovision.ai.data.local.dao.ScanDao
import com.agrovision.ai.data.local.entity.ScanEntity

@Database(
    entities = [ScanEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AgroVisionDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao

    companion object {
        const val DATABASE_NAME = "agrovision.db"
    }
}
