package com.example.spotyclone.states

import androidx.media3.common.MediaItem

data class PlayerState(
    var currentSong: String,
    var isPlaying: Boolean,
    val mediaItem: MediaItem?
)
