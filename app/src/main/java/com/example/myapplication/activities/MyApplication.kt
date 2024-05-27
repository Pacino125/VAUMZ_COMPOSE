package com.example.myapplication.activities

import android.app.Application
import androidx.room.Room
import com.example.myapplication.database.FishingLicense
import com.example.myapplication.repositories.AreaRepository
import com.example.myapplication.repositories.CatchRepository
import com.example.myapplication.repositories.FishTypeRepository
import com.example.myapplication.repositories.FishingSessionRepository
import com.example.myapplication.repositories.IAreaRepository
import com.example.myapplication.repositories.ICatchRepository
import com.example.myapplication.repositories.IFishTypeRepository
import com.example.myapplication.repositories.IFishingSessionRepository
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(module {
                    single {
                        Room.databaseBuilder(this@MyApplication, FishingLicense::class.java, "fishing_license").build()
                    }

                    single { AreaRepository(get()) } bind IAreaRepository::class
                    single { CatchRepository(get()) } bind ICatchRepository::class
                    single { FishingSessionRepository(get()) } bind IFishingSessionRepository::class
                    single { FishTypeRepository(get()) } bind IFishTypeRepository::class
            })
        }
    }
}