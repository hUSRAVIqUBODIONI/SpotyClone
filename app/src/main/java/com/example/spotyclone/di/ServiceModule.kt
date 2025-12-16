package com.example.spotyclone.di

import android.content.Context

import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.spotyclone.data.appWrite.remote.AppDataBase
import com.example.spotyclone.data.appWrite.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import io.appwrite.Client
import io.appwrite.services.Storage

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideMusicStorage( @ApplicationContext context: Context) : Storage{
         val client : Client = Client(context)
            .setEndpoint("https://fra.cloud.appwrite.io/v1")
            .setProject("6925ad180027b51a9d19")

         return Storage(client)
    }

    @Provides
    @ServiceScoped
    fun provideDataBase(
        @ApplicationContext context: Context
    ) : AppDataBase{
        return AppDataBase(context)
    }

    @Provides
    @ServiceScoped
    fun provideMusicRepository(
        appDataBase: AppDataBase
    ) : MusicRepository = MusicRepository(appDataBase)



    @Provides
    @ServiceScoped
    fun provideAudioAttributes()  = AudioAttributes.Builder()
        .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(USAGE_MEDIA)
        .build()

    @OptIn(UnstableApi::class)
    @Provides
    @ServiceScoped
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(audioAttributes, true)
            setHandleAudioBecomingNoisy(true)
        }
    }

}