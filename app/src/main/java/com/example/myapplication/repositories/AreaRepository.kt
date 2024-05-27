package com.example.myapplication.repositories

import com.example.myapplication.database.FishingLicense
import com.example.myapplication.entities.Area
import kotlinx.coroutines.flow.Flow

class AreaRepository(private val database: FishingLicense) : IAreaRepository {
    private val dao = database.areaDao()

    override suspend fun getAreasOrderedByName() : Flow<List<Area>> {
        return dao.getAreasOrderedByName()
    }

    override suspend fun getAreaById(id: Int): Flow<Area> {
        return dao.getAreaById(id)
    }
}