package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.entities.FishingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface IFishingSessionDao {
    @Upsert
    suspend fun upsertFishingSession(fishingSession: FishingSession)

    @Query("SELECT * FROM tbl_fishing_session ORDER BY date DESC")
    fun getFishingSessionsOrderedByDate() : Flow<List<FishingSession>>

    @Query("SELECT * FROM tbl_fishing_session WHERE isActive = 1")
    fun getActiveFishingSession() : Flow<FishingSession>
}