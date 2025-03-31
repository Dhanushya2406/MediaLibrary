package com.dhanu.medialibrarytask.allFolder

import android.content.Context

class RecentMediaRepository(context: Context) {
    private val recentMediaDao = RecentMediaDatabase.getDatabase(context).recentMediaDao()

    suspend fun insertMedia(media: RecentMediaEntity) {
        recentMediaDao.insertMedia(media)
    }

    suspend fun getAllMedia(): List<RecentMediaEntity> {
        return recentMediaDao.getAllMedia()
    }
}