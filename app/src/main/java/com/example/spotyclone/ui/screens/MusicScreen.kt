package com.example.spotyclone.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spotyclone.states.MusicListActions
import com.example.spotyclone.ui.utils.Player
import com.example.spotyclone.ui.viewmodels.MusicViewModel

@Composable
fun MusicScreen(
    viewModel: MusicViewModel = hiltViewModel()
){
    val plaerState = viewModel.playerState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.Center) {
        Text(text = plaerState.value.currentSong)
        Text(text = plaerState.value.isPlaying.toString())
        Text(text = plaerState.value.mediaItem!!.mediaMetadata.artist.toString())
    }

    Player(Modifier.fillMaxWidth(),plaerState.value,{viewModel.onEvent(it)})

}