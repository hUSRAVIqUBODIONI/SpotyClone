package com.example.spotyclone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.spotyclone.exoplayer.MediaBrowserController
import com.example.spotyclone.exoplayer.MusicControllerHolder
import com.example.spotyclone.states.MusicListActions
import com.example.spotyclone.states.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext

    private val _songs = MutableStateFlow<List<MediaItem>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState("Choose track",false,null))
    val playerState = _playerState.asStateFlow()

    private val mediaBrowserController = MediaBrowserController(appContext)



    private val controllerCallback = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            mediaItem?.let {
                _playerState.value = _playerState.value.copy(
                    currentSong = it.mediaMetadata.title.toString()
                )
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(
                isPlaying = isPlaying
            )
        }
    }

    init {
        viewModelScope.launch {
            mediaBrowserController.init()

            // Подключаемся к существующим объектам

            MusicControllerHolder.mediaController?.addListener(controllerCallback)
            _songs.value = mediaBrowserController.fetchData("root")


            _playerState.value = _playerState.value.copy(
                message = "hello"
            )
        }
    }

    fun onEvent(actions: MusicListActions){
        when(actions){
            is MusicListActions.onClick -> playSong(actions.index)
            MusicListActions.onNext -> onNext()
            MusicListActions.onPause ->onPause()
            MusicListActions.onPlay -> onPlay()
            MusicListActions.onPrev -> onPrev()
        }
    }


    fun playSong(index: Int) {
        if (_playerState.value.isPlaying && MusicControllerHolder.mediaController?.currentMediaItemIndex == index){
            onPause()
        }

        if(!_playerState.value.isPlaying && MusicControllerHolder.mediaController?.currentMediaItemIndex == index){
            onPlay()
        }

        if( MusicControllerHolder.mediaController?.currentMediaItemIndex != index){
            MusicControllerHolder.mediaController?.seekToDefaultPosition(index)

            onPlay()
        }
    }

    fun onPause(){
        MusicControllerHolder.mediaController?.pause()
    }

    fun onPlay(){
        MusicControllerHolder.mediaController?.play()
    }

    fun onNext(){
        val nextIndex = MusicControllerHolder.mediaController?.currentMediaItemIndex?.plus(1) ?: 0
        if (nextIndex >= _songs.value.size) playSong(0) else playSong(nextIndex)
    }

    fun onPrev(){
        val nextIndex = MusicControllerHolder.mediaController?.currentMediaItemIndex?.minus(1) ?: 0
        if (nextIndex >= 0) playSong(nextIndex) else playSong(_songs.value.size-1)
    }

    override fun onCleared() {
        super.onCleared()
    }

}
