package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.FishType
import kotlinx.coroutines.flow.Flow

class FishTypeRepository(private val database: FishingLicense) : IFishTypeRepository {
    private val dao = database.fishTypeDao()

    override suspend fun getFishTypes() : Flow<List<FishType>> {
        return dao.getFishTypes()
    }
}