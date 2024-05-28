package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Catch
import kotlinx.coroutines.flow.Flow

class CatchRepository(private val database: FishingLicense) : ICatchRepository {
    private val dao = database.catchDao()

    override suspend fun insertCatch(catch: Catch) {
        dao.insertCatch(catch)
    }

    override suspend fun getCatchById(id: Int): Flow<Catch> {
        return dao.getCatchById(id)
    }

    override fun getHighestId(): Flow<Int?> {
        return dao.getHighestId()
    }
}