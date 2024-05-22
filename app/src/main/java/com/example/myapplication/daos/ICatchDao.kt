package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.example.myapplication.entities.Catch

@Dao
interface ICatchDao {
    @Upsert
    suspend fun insertCatch(catch: Catch)
}