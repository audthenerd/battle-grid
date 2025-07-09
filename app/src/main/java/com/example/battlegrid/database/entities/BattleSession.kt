package com.example.battlegrid.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "battle_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"])
    ]
)
data class BattleSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val description: String? = null,
    val mapData: String? = null, // JSON string for map configuration
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isCompleted: Boolean = false,
    val playerCount: Int = 1,
    val difficulty: String = "Normal" // Easy, Normal, Hard
) 