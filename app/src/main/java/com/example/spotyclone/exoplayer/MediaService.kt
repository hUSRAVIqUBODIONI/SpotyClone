package com.example.spotyclone.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.example.spotyclone.MainActivity
import com.example.spotyclone.exoplayer.MusicControllerHolder
import com.example.spotyclone.data.appWrite.repository.MusicRepository
import com.example.spotyclone.data.db.repository.RoomRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    private var serviceJob = SupervisorJob()
    private var serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var librarySession: MediaLibrarySession

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var  roomRepository: RoomRepository


    @Inject
    lateinit var musicRepository: MusicRepository

    private var mediaItems: List<MediaItem> = emptyList()

    private var roomMediaItems: List<MediaItem> = emptyList()


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()



        createLibrarySession()



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


            val room_songs = roomRepository.getAllSongsOnce()
            Log.d("TestRoom", room_songs.toString() + "here")



            roomMediaItems = room_songs.map { song ->
                MediaItem.Builder()
                    .setUri(song.url)
                    .setMediaId(song.id.toString())
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
                exoPlayer.playWhenReady = false



                librarySession.notifyChildrenChanged(
                    "root",
                    mediaItems.size,
                    null
                )
                librarySession.notifyChildrenChanged(
                    "room",
                    roomMediaItems.size,
                    null
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun createLibrarySession() {
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

                    when(parentId){
                        "root" ->{

                            return Futures.immediateFuture(
                                LibraryResult.ofItemList(ImmutableList.copyOf(mediaItems), params)
                            )
                        }
                        "room" ->{

                            return Futures.immediateFuture(
                                LibraryResult.ofItemList(ImmutableList.copyOf(roomMediaItems), params)
                            )
                        }
                        else->{
                            return Futures.immediateFuture(
                                LibraryResult.ofItemList(ImmutableList.of(), params)
                            )
                        }
                    }
                }

                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {

                    val base = super.onConnect(session, controller)

                    val sessionCommands =
                        base.availableSessionCommands
                            .buildUpon()
                            .add(SessionCommand("PLAY_FROM_PARENT", Bundle.EMPTY))
                            .build()

                    return MediaSession.ConnectionResult.accept(
                        sessionCommands,
                        base.availablePlayerCommands
                    )
                }


                override fun onCustomCommand(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    customCommand: SessionCommand,
                    args: Bundle
                ): ListenableFuture<SessionResult> {

                    if (customCommand.customAction == "PLAY_FROM_PARENT") {

                        val parentId = args.getString("PARENT_ID") ?: return Futures.immediateFuture(
                            SessionResult(SessionError.ERROR_BAD_VALUE)
                        )

                        val index = args.getInt("INDEX", 0)

                        handlePlayFromParent(parentId, index)

                        return Futures.immediateFuture(
                            SessionResult(SessionResult.RESULT_SUCCESS)
                        )
                    }

                    return super.onCustomCommand(session, controller, customCommand, args)
                }

            }
        )
            .setSessionActivity(mainIntent)
            .build()

        setMediaNotificationProvider(DefaultMediaNotificationProvider(this))
    }



    private fun handlePlayFromParent(parentId: String, index: Int) {

        val newList = when (parentId) {
            "root" -> mediaItems
            "room" -> roomMediaItems
            else -> emptyList()
        }

        val needReset =
            MusicControllerHolder.currentParentId != parentId ||
                    exoPlayer.mediaItemCount == 0

        if (needReset) {
            exoPlayer.setMediaItems(newList, index, 0)
            exoPlayer.prepare()
            MusicControllerHolder.currentParentId = parentId
        } else {
            exoPlayer.seekToDefaultPosition(index)
        }

        exoPlayer.play()
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
