package com.example.spotyclone.exoplayer

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.spotyclone.service.MusicService
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await


class MediaBrowserController(val appContext: Context) {
    suspend fun init(){
        if(MusicControllerHolder.mediaController == null){
            val token = SessionToken(appContext, ComponentName(appContext, MusicService::class.java))
            delay(5000)
            val mediaBrowser = MediaBrowser.Builder(appContext, token).buildAsync().await()
            MusicControllerHolder.mediaBrowser = mediaBrowser

            val controller = MediaController.Builder(appContext, token).buildAsync().await()
            MusicControllerHolder.mediaController = controller
        }
    }

    suspend fun fetchData(parentID: String): List<MediaItem> {
        val mb = MusicControllerHolder.mediaBrowser ?: return emptyList()

        val result = mb.getChildren(parentID, 0, 100, null).await()

        return if (result.resultCode == LibraryResult.RESULT_SUCCESS) {
            result.value ?: emptyList()
        } else {
            emptyList()
        }
    }
}