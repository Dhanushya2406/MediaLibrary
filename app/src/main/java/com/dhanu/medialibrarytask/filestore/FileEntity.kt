package com.dhanu.medialibrarytask.filestore

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val firebaseUrl: String? = null
)
