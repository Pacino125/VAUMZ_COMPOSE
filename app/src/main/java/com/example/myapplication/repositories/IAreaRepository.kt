package com.example.myapplication.repositories

import com.example.myapplication.entities.Area
import kotlinx.coroutines.flow.Flow

interface IAreaRepository {
    suspend fun getAreasOrderedByName() : Flow<List<Area>>
    suspend fun getAreaById(id: Int) : Flow<Area>
}