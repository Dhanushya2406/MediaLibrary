package com.dhanu.medialibrarytask.filestore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("SELECT * FROM files WHERE firebaseUrl IS NULL") // Get unUploaded files
    suspend fun getUnUploadedFiles(): List<FileEntity>

    @Query("UPDATE files SET firebaseUrl = :url WHERE id = :id")
    suspend fun updateFileUrl(id: Int, url: String)
}