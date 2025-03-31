package com.dhanu.medialibrarytask.allFolder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: RecentMediaEntity)

    @Query("SELECT * FROM media_table")
    suspend fun getAllMedia(): List<RecentMediaEntity>
}