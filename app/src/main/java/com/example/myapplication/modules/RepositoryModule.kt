package com.example.myapplication.modules

import android.content.Context
import androidx.room.Room
import com.example.myapplication.daos.IFishingSessionAndCatchesDao
import com.example.myapplication.database.FishingLicense
import com.example.myapplication.repositories.FishingSessionsAndCatchesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FishingLicense {
        return Room.databaseBuilder(
            context.applicationContext,
            FishingLicense::class.java,
            "FishingLicense"
        ).build()
    }

    @Provides
    fun provideSessionDao(database: FishingLicense): IFishingSessionAndCatchesDao {
        return database.fishingSessionAndCatchDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideFishingSessionRepository(database: FishingLicense): FishingSessionsAndCatchesRepository {
        return FishingSessionsAndCatchesRepository(database)
    }
}