package com.example.myapplication.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.entities.Catch
import kotlinx.coroutines.flow.Flow

@Dao
interface ICatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(catches: Catch)

    @Query("SELECT * FROM tbl_catch WHERE id = :catchId")
    fun getCatchById(catchId: Int) : Flow<Catch>
}