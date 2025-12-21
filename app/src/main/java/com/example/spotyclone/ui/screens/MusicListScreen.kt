package com.example.spotyclone.ui.screens



import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import com.example.spotyclone.states.MusicListActions
import com.example.spotyclone.states.MusicListActions.onClick
import com.example.spotyclone.states.PlayerState
import com.example.spotyclone.ui.utils.Player

import com.example.spotyclone.viewmodel.MusicViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicScreenRoot(
    viewModel: MusicViewModel = hiltViewModel(),) {
    val songs by viewModel.songs.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(key1 = playerState.message) {
       playerState.message?.let {
           snackbar.showSnackbar(it)
       }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }
    ) {
        MusicScreen(
            songs = songs,
            playerState = playerState,
            action = {viewModel.onEvent(it)}
           )
    }
}


@Composable
fun MusicScreen(songs: List<MediaItem>,
                playerState: PlayerState,
                action:(MusicListActions) -> Unit){
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)

        ) {
            itemsIndexed(songs) { index, song ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {action(onClick(index))}),
                    text = song.mediaMetadata.title.toString()
                )
            }
        }
        Player(Modifier.fillMaxWidth(),playerState,action)
    }
}