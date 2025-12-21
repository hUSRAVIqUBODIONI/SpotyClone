package com.example.spotyclone.data.appWrite.remote

import android.content.Context
import android.util.Log
import com.example.spotyclone.data.appWrite.entities.Song
import com.example.spotyclone.data.appWrite.entities.toSong
import io.appwrite.Client
import io.appwrite.services.Databases

class AppDataBase(context: Context) {

    private val client : Client = Client(context)
        .setEndpoint("https://fra.cloud.appwrite.io/v1")
        .setProject("6925ad180027b51a9d19")

    private val databases = Databases(client)


    suspend fun getMusicList(): List<Song> {
        val musicList : List<Song> = try {
            databases
                .listDocuments(
                    databaseId = "music-db",
                    collectionId = "music_list"
                )
                .documents
                .map {
                    Log.e("MyTag", "$it")
                    it.data.toSong()
                }
        }catch (e: Exception) {
            Log.e("MyTag", "Error loading songs $e", e)
            emptyList()
        }



        Log.d("MyTag", "Response: $musicList")
        return musicList
    }
}