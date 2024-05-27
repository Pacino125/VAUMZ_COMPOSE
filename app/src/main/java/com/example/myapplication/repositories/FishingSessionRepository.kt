package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.FishingSession
import kotlinx.coroutines.flow.Flow

class FishingSessionRepository(private val database: FishingLicense) : IFishingSessionRepository {
    private val dao = database.sessionDao()

    override suspend fun upsertFishingSession(fishingSession: FishingSession) {
        dao.upsertFishingSession(fishingSession)
    }

    override suspend fun getFishingSessionsOrderedByDate(): Flow<List<FishingSession>> {
        return dao.getFishingSessionsOrderedByDate()
    }
}