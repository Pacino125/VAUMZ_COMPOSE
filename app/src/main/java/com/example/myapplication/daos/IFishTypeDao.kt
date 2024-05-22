package com.example.myapplication.daos

import androidx.room.Query
import com.example.myapplication.entities.FishType
import kotlinx.coroutines.flow.Flow

interface IFishTypeDao {
    @Query("SELECT * FROM tbl_fish_type")
    suspend fun getFishTypes() : Flow<List<FishType>>
}