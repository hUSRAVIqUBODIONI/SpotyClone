package com.example.spotyclone.di

import android.content.Context
import androidx.room.Room
import com.example.spotyclone.data.db.SongDataBase
import com.example.spotyclone.data.db.dao.SongDao
import com.example.spotyclone.data.db.repository.RoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDataBase(
        @ApplicationContext context: Context
    ) : SongDataBase {

        return Room.databaseBuilder(
            context,
            SongDataBase::class.java,
            "Song_database"
        ).build()
    }



    @Provides
    @Singleton
    fun provideSongDao(db : SongDataBase) : SongDao = db.SongDao()


    @Provides
    @Singleton
    fun provideRoomRepository(songDao: SongDao) : RoomRepository = RoomRepository(songDao)

}