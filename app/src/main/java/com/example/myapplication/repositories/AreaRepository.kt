package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Area
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AreaRepository @Inject constructor(private val database: FishingLicense) : IAreaRepository {
    private val dao = database.areaDao()

    override suspend fun getAreasOrderedByName() : Flow<List<Area>> {
        return dao.getAreasOrderedByName()
    }
}