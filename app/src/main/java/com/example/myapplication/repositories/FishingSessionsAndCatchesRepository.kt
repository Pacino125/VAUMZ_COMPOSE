package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.entities.FishingSessionWithCatch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FishingSessionsAndCatchesRepository @Inject constructor(private val database: FishingLicense) {
    private val dao = database.fishingSessionAndCatchDao()

    suspend fun upsertFishingSession(fishingSession: FishingSession) {
        dao.upsertFishingSession(fishingSession)
    }

    suspend fun getFishingSessionsOrderedByDate(): Flow<List<FishingSession>> {
        return dao.getFishingSessionsOrderedByDate()
    }

    suspend fun addCatchToFishingSession(catch: Catch, fishingSessionId: Int) {
        dao.insertCatch(catch)
        val fishingSession = dao.getFishingSession(fishingSessionId)
        fishingSession.catchId = catch.id
        dao.upsertFishingSession(fishingSession)
    }

    suspend fun getFishingSessionWithCatch(fishingSessionId: Int): FishingSessionWithCatch {
        return dao.getFishingSessionWithCatch(fishingSessionId)
    }
}