package com.example.battlegrid.repository

import android.util.Log
import com.example.battlegrid.database.dao.UserDao
import com.example.battlegrid.database.entities.User
import kotlinx.coroutines.flow.Flow
import android.database.sqlite.SQLiteConstraintException

class UserRepository(private val userDao: UserDao) {
    
    fun getAllActiveUsers(): Flow<List<User>> = userDao.getAllActiveUsers()
    
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    suspend fun createUser(username: String, email: String): Long {
        // Validate input
        if (username.isBlank() || username.length < 3 || username.length > 20) {
            throw IllegalArgumentException("Username must be between 3 and 20 characters")
        }
        
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IllegalArgumentException("Please enter a valid email address")
        }
        
        // Check username availability before creating
        if (!isUsernameAvailable(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        
        val user = User(
            username = username.trim(),
            email = email.trim()
        )
        
        return try {
            val userId = userDao.insertUser(user)
            Log.d("UserRepository", "User created successfully with ID: $userId")
            userId
        } catch (e: SQLiteConstraintException) {
            Log.e("UserRepository", "Constraint violation when creating user", e)
            throw IllegalArgumentException("Username already exists")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user", e)
            throw e
        }
    }
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun deactivateUser(userId: Long) = userDao.deactivateUser(userId)
    
    suspend fun getActiveUserCount(): Int = userDao.getActiveUserCount()
    
    suspend fun isUsernameAvailable(username: String): Boolean {
        return getUserByUsername(username.trim()) == null
    }
} 