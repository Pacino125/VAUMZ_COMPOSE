package com.example.myapplication.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.myapplication.daos.IAreaDao
import com.example.myapplication.daos.ICatchDao
import com.example.myapplication.daos.IFishTypeDao
import com.example.myapplication.daos.IFishingSessionDao
import com.example.myapplication.database.FishingLicense
import com.example.myapplication.repositories.AreaRepository
import com.example.myapplication.repositories.CatchRepository
import com.example.myapplication.repositories.FishTypeRepository
import com.example.myapplication.repositories.FishingSessionRepository
import com.example.myapplication.repositories.IAreaRepository
import com.example.myapplication.repositories.ICatchRepository
import com.example.myapplication.repositories.IFishTypeRepository
import com.example.myapplication.repositories.IFishingSessionRepository
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
    @ApplicationContext
    fun provideAppContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): FishingLicense {
        return Room.databaseBuilder(
            context.applicationContext,
            FishingLicense::class.java, "fishing_license"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAreaDao(database: FishingLicense): IAreaDao {
        return database.areaDao()
    }

    @Provides
    @Singleton
    fun provideCatchDao(database: FishingLicense): ICatchDao {
        return database.catchDao()
    }

    @Provides
    @Singleton
    fun provideFishingSessionDao(database: FishingLicense): IFishingSessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideFishTypeDao(database: FishingLicense): IFishTypeDao {
        return database.fishTypeDao()
    }

    @Provides
    @Singleton
    fun provideAreaRepository(database: FishingLicense): IAreaRepository {
        return AreaRepository(database)
    }

    @Provides
    @Singleton
    fun provideCatchRepository(database: FishingLicense): ICatchRepository {
        return CatchRepository(database)
    }

    @Provides
    @Singleton
    fun provideFishingSessionRepository(database: FishingLicense): IFishingSessionRepository {
        return FishingSessionRepository(database)
    }

    @Provides
    @Singleton
    fun provideFishTypeRepository(database: FishingLicense): IFishTypeRepository {
        return FishTypeRepository(database)
    }
}