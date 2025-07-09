package com.example.battlegrid.repository

import com.example.battlegrid.database.dao.NineLinerDao
import com.example.battlegrid.database.entities.NineLiner
import kotlinx.coroutines.flow.Flow

class NineLinerRepository(private val nineLinerDao: NineLinerDao) {
    
    fun getAllNineLiners(): Flow<List<NineLiner>> {
        return nineLinerDao.getAllNineLiners()
    }
    
    fun getDraftNineLiners(): Flow<List<NineLiner>> {
        return nineLinerDao.getNineLinersByStatus("DRAFT")
    }
    
    fun getTransmittedNineLiners(): Flow<List<NineLiner>> {
        return nineLinerDao.getNineLinersByStatus("TRANSMITTED")
    }
    
    fun getNineLinersByType(requestType: String): Flow<List<NineLiner>> {
        return nineLinerDao.getNineLinersByType(requestType)
    }
    
    suspend fun getNineLinerById(id: Long): NineLiner? {
        return nineLinerDao.getNineLinerById(id)
    }
    
    suspend fun saveDraft(
        requestType: String,
        line1: String,
        line2: String,
        line3: String,
        line4: String,
        line5: String,
        line6: String,
        line7: String,
        line8: String,
        line9: String,
        createdBy: Long? = null
    ): Long {
        val nineLiner = NineLiner(
            requestType = requestType,
            line1 = line1,
            line2 = line2,
            line3 = line3,
            line4 = line4,
            line5 = line5,
            line6 = line6,
            line7 = line7,
            line8 = line8,
            line9 = line9,
            status = "DRAFT",
            createdBy = createdBy
        )
        return nineLinerDao.insertNineLiner(nineLiner)
    }
    
    suspend fun updateNineLiner(nineLiner: NineLiner) {
        nineLinerDao.updateNineLiner(nineLiner)
    }
    
    suspend fun transmitNineLiner(id: Long): Boolean {
        return try {
            nineLinerDao.markAsTransmitted(id, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun archiveNineLiner(id: Long) {
        nineLinerDao.archiveNineLiner(id)
    }
    
    suspend fun deleteNineLiner(nineLiner: NineLiner) {
        nineLinerDao.deleteNineLiner(nineLiner)
    }
    
    suspend fun getTransmittedCount(): Int {
        return nineLinerDao.getTransmittedCount()
    }
    
    suspend fun cleanupOldArchived(daysToKeep: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        nineLinerDao.deleteOldArchivedRequests(cutoffTime)
    }
} 