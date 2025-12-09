package com.example.spotyclone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.example.spotyclone.MainActivity
import com.example.spotyclone.exoplayer.MusicControllerHolder
import com.example.spotyclone.repository.MusicRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import io.appwrite.services.Storage
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var librarySession: MediaLibrarySession

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var musicStorage: Storage

    @Inject
    lateinit var musicRepository: MusicRepository

    private var mediaItems: List<MediaItem> = emptyList()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        startForeground(1, createForegroundNotification())
        // Создаём сессию сразу с пустым списком
        createLibrarySession(emptyList())

        serviceScope.launch {
            val songs = musicRepository.getMusicList()
            Log.d("MusicService", "Loaded songs: $songs")

            mediaItems = songs.map { song ->
                val url = "https://fra.cloud.appwrite.io/v1/storage/buckets/music-bucket/files/${song.song_url}/view?project=6925ad180027b51a9d19&mode=admin"
                MediaItem.Builder()
                    .setUri(url)
                    .setMediaId(song.id)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            .setIsBrowsable(false)
                            .setIsPlayable(true)
                            .build()
                    )
                    .build()
            }


            withContext(Dispatchers.Main) {
                exoPlayer.setMediaItems(mediaItems)
                exoPlayer.prepare()
                librarySession.notifyChildrenChanged(
                    "root",
                    mediaItems.size,
                    null
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun createLibrarySession(initialItems: List<MediaItem>) {
        val mainIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        librarySession = MediaLibrarySession.Builder(
            this,
            exoPlayer,
            object : MediaLibrarySession.Callback{
                override fun onGetChildren(
                    session: MediaLibraryService.MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    parentId: String,
                    page: Int,
                    pageSize: Int,
                    params: MediaLibraryService.LibraryParams?
                ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                    return Futures.immediateFuture(
                        LibraryResult.ofItemList(ImmutableList.copyOf(mediaItems), params)
                    )
                }
            }
        )
            .setSessionActivity(mainIntent)
            .build()

        setMediaNotificationProvider(DefaultMediaNotificationProvider(this))
    }

    private fun createForegroundNotification(): Notification {
        val channelId = "music_service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Загрузка музыки…")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        librarySession

    override fun onDestroy() {
        MusicControllerHolder.mediaController?.release()
        MusicControllerHolder.mediaBrowser?.release()
        MusicControllerHolder.mediaController = null
        MusicControllerHolder.mediaBrowser = null
        exoPlayer.release()
        librarySession.release()
        serviceScope.cancel()
        super.onDestroy()
    }
}
