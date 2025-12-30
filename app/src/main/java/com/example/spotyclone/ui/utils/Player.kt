package com.example.spotyclone.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spotyclone.states.MusicListActions
import com.example.spotyclone.states.MusicListActions.onNext
import com.example.spotyclone.states.MusicListActions.onPause
import com.example.spotyclone.states.MusicListActions.onPlay
import com.example.spotyclone.states.MusicListActions.onPrev
import com.example.spotyclone.states.PlayerState

@Composable
fun Player(
    modifier: Modifier,
    playerState : PlayerState,
    action: (MusicListActions) -> Unit
){
    Card(modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = {
            action(MusicListActions.onNavigate)
        })
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
        )  {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                text =  playerState.currentSong
            )
            Spacer(Modifier.size(10.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = {action(onPrev)}) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.ArrowBack
                    )
                }

                if (playerState.isPlaying){
                    IconButton(onClick = {action(onPause)}){
                        Icon(
                            contentDescription = null,
                            imageVector = Icons.Default.PlayArrow
                        )
                    }
                }else{
                    IconButton(onClick = {action(onPlay)}) {
                        Icon(
                            contentDescription = null,
                            imageVector = Icons.Default.Lock
                        )
                    }
                }

                IconButton(onClick = {action(onNext)}) {
                    Icon(
                        contentDescription = null,
                        imageVector = Icons.Default.ArrowForward
                    )
                }
            }
        }
    }
}


