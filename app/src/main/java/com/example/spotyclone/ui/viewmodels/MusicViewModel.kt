package com.example.spotyclone.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.spotyclone.exoplayer.MusicControllerHolder.mediaController
import com.example.spotyclone.states.MusicListActions
import com.example.spotyclone.states.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class MusicViewModel @Inject constructor(): ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState(
        isPlaying = false,
        currentSong = "notChoosed",
        mediaItem = null
    ))
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    private val controller = mediaController
    private val controllerCallback = object : Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(
                isPlaying = isPlaying
            )
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            mediaItem?.let {
                _playerState.value = _playerState.value.copy(
                    currentSong = it.mediaMetadata.title.toString()
                )
            }
        }
    }

    init {
        controller?.addListener(controllerCallback)
        synchronizePlayerState()

    }

    private fun synchronizePlayerState() {


        _playerState.value = PlayerState(
            isPlaying = controller?.isPlaying ?: false,
            currentSong = controller?.currentMediaItem
                ?.mediaMetadata
                ?.title
                ?.toString()
                .orEmpty(),
            mediaItem = controller?.currentMediaItem
        )
    }

    fun onEvent(actions: MusicListActions){
        when(actions){
            is MusicListActions.onClick -> playSong(actions.index)
            MusicListActions.onNext -> onNext()
            MusicListActions.onPause ->onPause()
            MusicListActions.onPlay -> onPlay()
            MusicListActions.onPrev -> onPrev()
            is MusicListActions.onNavigate -> {}
        }
    }


    fun playSong(index: Int){
        mediaController?.seekToDefaultPosition(index)

    }

    fun onPause(){
        mediaController?.pause()
    }

    fun onPlay(){
        mediaController?.play()
    }

    fun onNext(){
        val nextIndex = mediaController?.currentMediaItemIndex?.plus(1) ?: 0
        val mediaCount = mediaController?.mediaItemCount ?: 0
        if(nextIndex >= mediaCount){
            playSong(0)
        }else{
            playSong(nextIndex)
        }
    }

    fun onPrev(){
        val nextIndex = mediaController?.currentMediaItemIndex?.minus(1) ?: 0
        val mediaCount = mediaController?.mediaItemCount ?: 0
        if(nextIndex < 0){
            playSong(mediaCount-1)
        }else{
            playSong(nextIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}