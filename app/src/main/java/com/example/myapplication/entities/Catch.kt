package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_catch")
data class Catch (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fishType : FishType,
    val fishCount : Int,
    val length : Int?,
    val weight : Double
)