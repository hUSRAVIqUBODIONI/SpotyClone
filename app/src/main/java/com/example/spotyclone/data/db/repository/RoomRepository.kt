package com.example.spotyclone.data.db.repository


import com.example.spotyclone.data.db.dao.SongDao
import com.example.spotyclone.data.db.entities.SongEntity
import kotlinx.coroutines.flow.first

class RoomRepository(private val songDao: SongDao) {

    val songs = songDao.getSongs()

    suspend fun insertSong(songEntity: SongEntity) = songDao.insertSong(songEntity)

    suspend fun deleteSong(songEntity: SongEntity) = songDao.deleteSong(songEntity)

    suspend fun getAllSongsOnce(): List<SongEntity> {
        return songDao.getSongs().first()
    }
}