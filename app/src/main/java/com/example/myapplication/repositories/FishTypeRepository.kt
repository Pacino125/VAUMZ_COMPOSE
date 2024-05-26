package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.FishType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FishTypeRepository @Inject constructor(private val database: FishingLicense) : IFishTypeRepository {
    private val dao = database.fishTypeDao()

    override suspend fun getFishTypes() : Flow<List<FishType>> {
        return dao.getFishTypes()
    }
}