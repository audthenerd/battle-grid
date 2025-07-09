package com.example.battlegrid.database.dao

import androidx.room.*
import com.example.battlegrid.database.entities.GridPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface GridPositionDao {
    
    @Query("SELECT * FROM grid_positions WHERE sessionId = :sessionId AND isActive = 1 ORDER BY entityType, entityName")
    fun getSessionPositions(sessionId: Long): Flow<List<GridPosition>>
    
    @Query("SELECT * FROM grid_positions WHERE id = :positionId")
    suspend fun getPositionById(positionId: Long): GridPosition?
    
    @Query("SELECT * FROM grid_positions WHERE sessionId = :sessionId AND entityType = :entityType AND isActive = 1")
    fun getPositionsByType(sessionId: Long, entityType: String): Flow<List<GridPosition>>
    
    @Query("SELECT * FROM grid_positions WHERE sessionId = :sessionId AND xPosition = :x AND yPosition = :y AND isActive = 1")
    suspend fun getPositionAt(sessionId: Long, x: Int, y: Int): List<GridPosition>
    
    @Insert
    suspend fun insertPosition(position: GridPosition): Long
    
    @Insert
    suspend fun insertPositions(positions: List<GridPosition>)
    
    @Update
    suspend fun updatePosition(position: GridPosition)
    
    @Delete
    suspend fun deletePosition(position: GridPosition)
    
    @Query("UPDATE grid_positions SET isActive = 0 WHERE id = :positionId")
    suspend fun deactivatePosition(positionId: Long)
    
    @Query("DELETE FROM grid_positions WHERE sessionId = :sessionId")
    suspend fun clearSessionPositions(sessionId: Long)
    
    @Query("UPDATE grid_positions SET xPosition = :newX, yPosition = :newY, updatedAt = :timestamp WHERE id = :positionId")
    suspend fun movePosition(positionId: Long, newX: Int, newY: Int, timestamp: Long = System.currentTimeMillis())
} 