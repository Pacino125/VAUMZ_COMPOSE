package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.example.myapplication.entities.Catch

@Dao
interface ICatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(catches: Catch)
}