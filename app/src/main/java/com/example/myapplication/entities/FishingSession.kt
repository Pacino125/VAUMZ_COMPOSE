package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_fishing_session")
data class FishingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val areaId: Int,
    val date: Long,
    var isActive: Boolean,
    val catchId: Int?
)