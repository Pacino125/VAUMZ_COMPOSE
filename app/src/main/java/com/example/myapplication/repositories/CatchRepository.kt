package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Catch
import javax.inject.Inject

class CatchRepository @Inject constructor(private val database: FishingLicense) : ICatchRepository {
    private val dao = database.catchDao()

    override suspend fun insertCatch(catch: Catch) {
        dao.insertCatch(catch)
    }
}