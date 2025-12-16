package com.example.spotyclone.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songTable")
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val url: String
)