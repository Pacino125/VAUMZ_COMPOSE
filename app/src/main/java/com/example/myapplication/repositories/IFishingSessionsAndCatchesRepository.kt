package com.example.myapplication.repositories

import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.entities.FishingSessionWithCatch
import kotlinx.coroutines.flow.Flow

interface IFishingSessionsAndCatchesRepository {
    suspend fun upsertFishingSession(fishingSession: FishingSession)
    fun getFishingSessionsOrderedByDate(): Flow<List<FishingSession>>
    suspend fun addCatchToFishingSession(catch: Catch, fishingSessionId: Int)
    suspend fun getFishingSessionWithCatch(fishingSessionId: Int): FishingSessionWithCatch
}