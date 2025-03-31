package com.dhanu.medialibrarytask.allFolder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_table")
data class RecentMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val fileType: String,
    val fileName: String
)


