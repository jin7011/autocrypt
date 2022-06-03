package com.di.autocrypt.di

import android.content.Context
import com.di.autocrypt.data.CenterDAO
import com.di.autocrypt.data.CenterDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): CenterDB {
        return CenterDB.getInstance(context)
    }

    @Provides
    fun providePlantDao(appDatabase: CenterDB): CenterDAO {
        return appDatabase.centerDAO()
    }
}