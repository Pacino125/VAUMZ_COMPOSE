package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.entities.FishingSessionWithCatch
import kotlinx.coroutines.flow.Flow

@Dao
interface IFishingSessionAndCatchesDao {
    @Upsert
    suspend fun upsertFishingSession(fishingSession: FishingSession)

    @Query("SELECT * FROM tbl_fishing_session ORDER BY date ASC")
    suspend fun getFishingSessionsOrderedByDate() : Flow<List<FishingSession>>

    @Transaction
    @Query("SELECT * FROM tbl_fishing_session WHERE id = :fishingSessionId")
    suspend fun getFishingSessionWithCatch(fishingSessionId: Int): FishingSessionWithCatch

    @Query("SELECT * FROM tbl_fishing_session WHERE id = :fishingSessionId")
    suspend fun getFishingSession(fishingSessionId: Int): FishingSession

    @Upsert
    suspend fun insertCatch(catch: Catch)
}