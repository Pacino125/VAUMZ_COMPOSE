package com.example.myapplication.repositories

import com.example.myapplication.entities.FishingSession
import kotlinx.coroutines.flow.Flow

interface IFishingSessionRepository {
    suspend fun upsertFishingSession(fishingSession: FishingSession)
    suspend fun getFishingSessionsOrderedByDate() : Flow<List<FishingSession>>
    suspend fun getActiveSession() : Flow<FishingSession>
}