package com.example.spotyclone.data.appWrite.entities


data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val song_url: String,

)


fun Map<String,Any>.toSong() =
    Song(
        id = this["\$id"] as String,
        title = this["title"] as String,
        genre = this["genre"] as String,
        artist = this["artist"] as String,
        song_url = this["song_url"] as String,

    )