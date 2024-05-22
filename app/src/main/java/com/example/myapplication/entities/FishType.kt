package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_fish_type")
data class FishType (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type : String,
    val minCatchLength : Int?,
    val maxCatchLength : Int?,
)