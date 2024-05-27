package com.example.myapplication.repositories

import com.example.myapplication.entities.FishType
import kotlinx.coroutines.flow.Flow

interface IFishTypeRepository {
    suspend fun getFishTypes() : Flow<List<FishType>>
    suspend fun getFishTypeById(id: Int) : Flow<FishType>
}