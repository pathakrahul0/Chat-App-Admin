package com.adminapp.di

import android.content.Context
import com.adminapp.prefrences.Preference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Prefernces {
    @Singleton
    @Provides
    fun providePreferenceBuilder(@ApplicationContext context: Context): Preference{
        return Preference(context = context)
    }
}