package com.dhanu.medialibrarytask.allFolder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentMediaEntity::class], version = 1)
abstract class RecentMediaDatabase: RoomDatabase() {
    abstract fun recentMediaDao() : RecentMediaDao

    companion object {
        @Volatile
        private var INSTANCE: RecentMediaDatabase? = null

        fun getDatabase(context: Context): RecentMediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecentMediaDatabase::class.java,
                    "media_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}