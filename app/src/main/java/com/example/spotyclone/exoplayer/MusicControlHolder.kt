package com.example.spotyclone.exoplayer

import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController

object MusicControllerHolder {
    var mediaController: MediaController? = null
    var mediaBrowser: MediaBrowser? = null

    var currentParentId: String? = null
}