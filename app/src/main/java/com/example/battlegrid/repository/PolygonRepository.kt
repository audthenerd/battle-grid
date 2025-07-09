package com.example.battlegrid.repository

import com.example.battlegrid.database.dao.PolygonDao
import com.example.battlegrid.database.entities.Polygon
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class CoordinatePoint(val latitude: Double, val longitude: Double)

class PolygonRepository(private val polygonDao: PolygonDao) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun getAllVisiblePolygons(): Flow<List<Polygon>> = polygonDao.getAllVisiblePolygons()
    
    fun getAllPolygons(): Flow<List<Polygon>> = polygonDao.getAllPolygons()
    
    suspend fun getPolygonById(polygonId: Long): Polygon? = polygonDao.getPolygonById(polygonId)
    
    suspend fun savePolygon(
        coordinates: List<LatLng>,
        name: String? = null,
        fillColor: String = "#3300FF",
        strokeColor: String = "#0000FF",
        strokeWidth: Float = 3f,
        fillAlpha: Float = 0.3f
    ): Long {
        val coordinatePoints = coordinates.map { CoordinatePoint(it.latitude, it.longitude) }
        val coordinatesJson = json.encodeToString(coordinatePoints)
        
        val polygon = Polygon(
            name = name,
            coordinates = coordinatesJson,
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
            fillAlpha = fillAlpha
        )
        
        return polygonDao.insertPolygon(polygon)
    }
    
    suspend fun updatePolygon(polygon: Polygon) = polygonDao.updatePolygon(polygon)
    
    suspend fun deletePolygon(polygon: Polygon) = polygonDao.deletePolygon(polygon)
    
    suspend fun deletePolygonById(polygonId: Long) = polygonDao.deletePolygonById(polygonId)
    
    suspend fun deleteAllPolygons() = polygonDao.deleteAllPolygons()
    
    suspend fun updatePolygonVisibility(polygonId: Long, isVisible: Boolean) = 
        polygonDao.updatePolygonVisibility(polygonId, isVisible)
    
    suspend fun getVisiblePolygonCount(): Int = polygonDao.getVisiblePolygonCount()
    
    fun parseCoordinates(coordinatesJson: String): List<LatLng> {
        return try {
            val coordinatePoints = json.decodeFromString<List<CoordinatePoint>>(coordinatesJson)
            coordinatePoints.map { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun serializeCoordinates(coordinates: List<LatLng>): String {
        val coordinatePoints = coordinates.map { CoordinatePoint(it.latitude, it.longitude) }
        return json.encodeToString(coordinatePoints)
    }
} 