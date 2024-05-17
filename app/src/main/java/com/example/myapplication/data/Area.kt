package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_area")
data class Area(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val areaId: String,
    val chap: Boolean?
)