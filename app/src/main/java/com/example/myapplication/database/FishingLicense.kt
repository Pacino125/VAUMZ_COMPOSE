package com.example.myapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.daos.IAreaDao
import com.example.myapplication.daos.ICatchDao
import com.example.myapplication.daos.IFishTypeDao
import com.example.myapplication.daos.IFishingSessionDao
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishType
import com.example.myapplication.entities.FishingSession

@Database(
    entities = [Area::class, Catch::class, FishingSession::class, FishType::class],
    version = 1
)
abstract class FishingLicense: RoomDatabase() {

    abstract fun areaDao() : IAreaDao
    abstract fun catchDao() : ICatchDao
    abstract fun sessionDao() : IFishingSessionDao
    abstract fun fishTypeDao() : IFishTypeDao
}