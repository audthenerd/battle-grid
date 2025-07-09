package com.example.battlegrid.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nine_liners")
data class NineLiner(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requestType: String, // CAS, MEDEVAC, FIRE SUPPORT
    val line1: String = "", // IP/BP (Initial Point/Battle Position)
    val line2: String = "", // Heading/Offset
    val line3: String = "", // Distance
    val line4: String = "", // Target Elevation
    val line5: String = "", // Target Description
    val line6: String = "", // Target Location (Grid)
    val line7: String = "", // Type Mark
    val line8: String = "", // Location of Friendlies
    val line9: String = "", // Egress
    val status: String = "DRAFT", // DRAFT, TRANSMITTED, ARCHIVED
    val createdAt: Long = System.currentTimeMillis(),
    val transmittedAt: Long? = null,
    val createdBy: Long? = null // Foreign key to User table
) 