package com.example.myapplication.repositories

import com.example.myapplication.entities.Catch

interface ICatchRepository {
    suspend fun insertCatch(catch: Catch)
}