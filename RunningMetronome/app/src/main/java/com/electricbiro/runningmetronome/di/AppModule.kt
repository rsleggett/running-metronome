package com.electricbiro.runningmetronome.di

import android.content.Context
import com.electricbiro.runningmetronome.audio.MetronomeAudioPlayer
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
    fun provideMetronomeAudioPlayer(
        @ApplicationContext context: Context
    ): MetronomeAudioPlayer {
        return MetronomeAudioPlayer(context)
    }
}
