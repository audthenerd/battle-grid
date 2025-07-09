package com.example.battlegrid.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.battlegrid.database.entities.User
import com.example.battlegrid.database.entities.BattleSession
import com.example.battlegrid.database.entities.GridPosition
import com.example.battlegrid.database.entities.Polygon
import com.example.battlegrid.database.entities.NineLiner
import com.example.battlegrid.database.dao.UserDao
import com.example.battlegrid.database.dao.BattleSessionDao
import com.example.battlegrid.database.dao.GridPositionDao
import com.example.battlegrid.database.dao.PolygonDao
import com.example.battlegrid.database.dao.NineLinerDao

@Database(
    entities = [User::class, BattleSession::class, GridPosition::class, Polygon::class, NineLiner::class],
    version = 5,
    exportSchema = false
)
abstract class BattleGridDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun battleSessionDao(): BattleSessionDao
    abstract fun gridPositionDao(): GridPositionDao
    abstract fun polygonDao(): PolygonDao
    abstract fun nineLinerDao(): NineLinerDao
    
    companion object {
        @Volatile
        private var INSTANCE: BattleGridDatabase? = null
        
        fun getDatabase(context: Context): BattleGridDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BattleGridDatabase::class.java,
                    "battle_grid_database"
                )
                    .fallbackToDestructiveMigration() // For development
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Enable foreign key constraints
                            db.execSQL("PRAGMA foreign_keys=ON")
                        }
                        
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Enable foreign key constraints
                            db.execSQL("PRAGMA foreign_keys=ON")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 