package com.agrovision.ai.data.repository

import com.agrovision.ai.core.common.DispatcherProvider
import com.agrovision.ai.data.local.dao.ScanDao
import com.agrovision.ai.data.local.entity.ScanEntity
import com.agrovision.ai.domain.model.Diagnosis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val scanDao: ScanDao,
    private val dispatchers: DispatcherProvider
) {

    fun getAllScans(): Flow<List<Diagnosis>> {
        return scanDao.getAllScans()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    fun getLatestScan(): Flow<Diagnosis?> {
        return scanDao.getLatestScan()
            .map { it?.toDomain() }
            .flowOn(dispatchers.io)
    }

    fun getScanCount(): Flow<Int> {
        return scanDao.getScanCount()
            .flowOn(dispatchers.io)
    }

    suspend fun saveScan(diagnosis: Diagnosis) = withContext(dispatchers.io) {
        scanDao.insertScan(ScanEntity.fromDomain(diagnosis))
    }

    suspend fun deleteScan(id: String) = withContext(dispatchers.io) {
        scanDao.deleteScanById(id)
    }

    suspend fun updateFeedback(id: String, feedbackSubmitted: Boolean) = withContext(dispatchers.io) {
        scanDao.updateFeedback(id, feedbackSubmitted)
    }
}
