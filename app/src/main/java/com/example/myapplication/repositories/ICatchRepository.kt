package com.example.myapplication.repositories

import com.example.myapplication.entities.Catch
import kotlinx.coroutines.flow.Flow

interface ICatchRepository {
    suspend fun insertCatch(catch: Catch)
    suspend fun getCatchById(id: Int) : Flow<Catch>
}