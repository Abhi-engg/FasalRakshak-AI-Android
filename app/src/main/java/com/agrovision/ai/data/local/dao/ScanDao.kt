package com.agrovision.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agrovision.ai.data.local.entity.ScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: String): ScanEntity?

    @Query("SELECT * FROM scans ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScan(): Flow<ScanEntity?>

    @Query("SELECT COUNT(*) FROM scans")
    fun getScanCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity)

    @Delete
    suspend fun deleteScan(scan: ScanEntity)

    @Query("DELETE FROM scans WHERE id = :id")
    suspend fun deleteScanById(id: String)

    @Query("UPDATE scans SET feedbackSubmitted = :feedbackSubmitted WHERE id = :id")
    suspend fun updateFeedback(id: String, feedbackSubmitted: Boolean)
}
