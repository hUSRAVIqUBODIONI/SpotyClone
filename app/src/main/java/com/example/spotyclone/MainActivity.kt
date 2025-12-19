package com.example.spotyclone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import com.example.spotyclone.service.MusicService
import com.example.spotyclone.ui.screens.MusicScreenRoot
import com.example.spotyclone.ui.theme.SpotyCloneTheme
import com.example.spotyclone.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        startService(Intent(this, MusicService::class.java))


        setContent {
            SpotyCloneTheme {
                Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { padding ->
                    MusicScreenRoot(padding)
                }
            }
        }
    }
}

