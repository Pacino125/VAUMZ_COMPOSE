package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.entities.FishType
import kotlinx.coroutines.flow.Flow

@Dao
interface IFishTypeDao {
    @Query("SELECT * FROM tbl_fish_type ORDER BY type ASC")
    fun getFishTypes() : Flow<List<FishType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fishTypes: List<FishType>)

    @Query("SELECT * FROM tbl_fish_type WHERE id = :fishTypeId")
    fun getFishTypeById(fishTypeId: Int) : Flow<FishType>
}