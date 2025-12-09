package com.example.spotyclone.states

sealed interface MusicListActions {
    data class onClick(val index: Int) : MusicListActions
    data object onPause : MusicListActions
    data object onPlay : MusicListActions
    data object onNext : MusicListActions
    data object onPrev : MusicListActions

}