package com.example.battlegrid.database.dao

import androidx.room.*
import com.example.battlegrid.database.entities.Polygon
import kotlinx.coroutines.flow.Flow

@Dao
interface PolygonDao {
    
    @Query("SELECT * FROM polygons WHERE isVisible = 1 ORDER BY createdAt ASC")
    fun getAllVisiblePolygons(): Flow<List<Polygon>>
    
    @Query("SELECT * FROM polygons ORDER BY createdAt ASC")
    fun getAllPolygons(): Flow<List<Polygon>>
    
    @Query("SELECT * FROM polygons WHERE id = :polygonId")
    suspend fun getPolygonById(polygonId: Long): Polygon?
    
    @Insert
    suspend fun insertPolygon(polygon: Polygon): Long
    
    @Insert
    suspend fun insertPolygons(polygons: List<Polygon>)
    
    @Update
    suspend fun updatePolygon(polygon: Polygon)
    
    @Delete
    suspend fun deletePolygon(polygon: Polygon)
    
    @Query("DELETE FROM polygons WHERE id = :polygonId")
    suspend fun deletePolygonById(polygonId: Long)
    
    @Query("DELETE FROM polygons")
    suspend fun deleteAllPolygons()
    
    @Query("UPDATE polygons SET isVisible = :isVisible WHERE id = :polygonId")
    suspend fun updatePolygonVisibility(polygonId: Long, isVisible: Boolean)
    
    @Query("UPDATE polygons SET fillColor = :fillColor, strokeColor = :strokeColor, updatedAt = :timestamp WHERE id = :polygonId")
    suspend fun updatePolygonColors(polygonId: Long, fillColor: String, strokeColor: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM polygons WHERE isVisible = 1")
    suspend fun getVisiblePolygonCount(): Int
} 