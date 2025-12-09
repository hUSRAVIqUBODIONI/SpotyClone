package com.example.spotyclone.states

data class PlayerState(
    var currentSong: String,
    var isPlaying: Boolean,
    val message: String?
)
