package com.example.spotyclone.data.appWrite.repository

import com.example.spotyclone.data.appWrite.remote.AppDataBase

class MusicRepository(private val appDataBase: AppDataBase) {
    suspend fun getMusicList() = appDataBase.getMusicList()
}