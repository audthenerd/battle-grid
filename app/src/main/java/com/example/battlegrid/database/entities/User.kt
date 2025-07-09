package com.example.battlegrid.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val avatar: String? = null,
    val level: Int = 1,
    val experience: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) 