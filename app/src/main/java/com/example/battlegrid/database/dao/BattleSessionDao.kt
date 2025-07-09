package com.example.battlegrid.database.dao

import androidx.room.*
import com.example.battlegrid.database.entities.BattleSession
import kotlinx.coroutines.flow.Flow

@Dao
interface BattleSessionDao {
    
    @Query("SELECT * FROM battle_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getUserSessions(userId: Long): Flow<List<BattleSession>>
    
    @Query("SELECT * FROM battle_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): BattleSession?
    
    @Query("SELECT * FROM battle_sessions WHERE isCompleted = 0 AND userId = :userId")
    fun getActiveSessions(userId: Long): Flow<List<BattleSession>>
    
    @Insert
    suspend fun insertSession(session: BattleSession): Long
    
    @Update
    suspend fun updateSession(session: BattleSession)
    
    @Delete
    suspend fun deleteSession(session: BattleSession)
    
    @Query("UPDATE battle_sessions SET isCompleted = 1, endTime = :endTime WHERE id = :sessionId")
    suspend fun completeSession(sessionId: Long, endTime: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM battle_sessions WHERE userId = :userId AND isCompleted = 1")
    suspend fun deleteCompletedSessions(userId: Long)
} 