package com.di.autocrypt.di

import com.di.autocrypt.data.CenterDAO
import com.di.autocrypt.data.CenterDB
import com.di.autocrypt.model.Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideService(): Service{
        return Service.create()
    }

}