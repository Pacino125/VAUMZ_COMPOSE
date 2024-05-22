package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.myapplication.entities.Area
import kotlinx.coroutines.flow.Flow

@Dao
interface IAreaDao {
    @Query("SELECT * FROM tbl_area ORDER BY name ASC")
    suspend fun getAreasOrderedByName() : Flow<List<Area>>
}