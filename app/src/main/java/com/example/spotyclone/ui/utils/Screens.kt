package com.example.spotyclone.ui.utils

sealed class Screens(val route:String, val title: String, val screenParam:String? = null) {
    object AppWriteScreen : Screens("appWrite","Music","root")
    object LocalMusicScreen : Screens("local","Saved Music", "room")
}