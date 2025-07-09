package com.example.battlegrid.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grid_positions",
    foreignKeys = [
        ForeignKey(
            entity = BattleSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"])
    ]
)
data class GridPosition(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val entityName: String, // Player name, monster name, etc.
    val entityType: String, // "player", "monster", "npc", "object"
    val xPosition: Int,
    val yPosition: Int,
    val health: Int = 100,
    val maxHealth: Int = 100,
    val isActive: Boolean = true,
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
) 