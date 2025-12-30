package com.example.spotyclone.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spotyclone.data.db.dao.SongDao
import com.example.spotyclone.data.db.entities.SongEntity

@Database(entities = [(SongEntity::class)], version = 1)
abstract class SongDataBase : RoomDatabase(){
    abstract fun SongDao(): SongDao
}