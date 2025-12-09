package com.example.spotyclone.repository

import com.example.spotyclone.data.remote.DataBase

class MusicRepository(private val dataBase: DataBase) {
    suspend fun getMusicList() = dataBase.getMusicList()
}