package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.entities.Area
import kotlinx.coroutines.flow.Flow

@Dao
interface IAreaDao {
    @Query("SELECT * FROM tbl_area ORDER BY name ASC")
    fun getAreasOrderedByName() : Flow<List<Area>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(areas: List<Area>)
}