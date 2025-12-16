package com.example.spotyclone.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.spotyclone.data.db.entities.SongEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Query("Select * From songTable")
    fun getSongs() : Flow<List<SongEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)
}