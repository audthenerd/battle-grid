package com.example.battlegrid.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "polygons")
data class Polygon(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = null,
    val coordinates: String, // JSON string storing LatLng coordinates
    val fillColor: String = "#3300FF", // Hex color for fill
    val strokeColor: String = "#0000FF", // Hex color for stroke
    val strokeWidth: Float = 3f,
    val fillAlpha: Float = 0.3f,
    val isVisible: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 