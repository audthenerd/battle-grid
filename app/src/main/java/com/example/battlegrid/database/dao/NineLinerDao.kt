package com.example.battlegrid.database.dao

import androidx.room.*
import com.example.battlegrid.database.entities.NineLiner
import kotlinx.coroutines.flow.Flow

@Dao
interface NineLinerDao {
    
    @Query("SELECT * FROM nine_liners ORDER BY createdAt DESC")
    fun getAllNineLiners(): Flow<List<NineLiner>>
    
    @Query("SELECT * FROM nine_liners WHERE status = :status ORDER BY createdAt DESC")
    fun getNineLinersByStatus(status: String): Flow<List<NineLiner>>
    
    @Query("SELECT * FROM nine_liners WHERE requestType = :requestType ORDER BY createdAt DESC")
    fun getNineLinersByType(requestType: String): Flow<List<NineLiner>>
    
    @Query("SELECT * FROM nine_liners WHERE id = :id")
    suspend fun getNineLinerById(id: Long): NineLiner?
    
    @Insert
    suspend fun insertNineLiner(nineLiner: NineLiner): Long
    
    @Update
    suspend fun updateNineLiner(nineLiner: NineLiner)
    
    @Delete
    suspend fun deleteNineLiner(nineLiner: NineLiner)
    
    @Query("UPDATE nine_liners SET status = 'TRANSMITTED', transmittedAt = :timestamp WHERE id = :id")
    suspend fun markAsTransmitted(id: Long, timestamp: Long)
    
    @Query("UPDATE nine_liners SET status = 'ARCHIVED' WHERE id = :id")
    suspend fun archiveNineLiner(id: Long)
    
    @Query("SELECT COUNT(*) FROM nine_liners WHERE status = 'TRANSMITTED'")
    suspend fun getTransmittedCount(): Int
    
    @Query("DELETE FROM nine_liners WHERE status = 'ARCHIVED' AND createdAt < :cutoffTime")
    suspend fun deleteOldArchivedRequests(cutoffTime: Long)
} 