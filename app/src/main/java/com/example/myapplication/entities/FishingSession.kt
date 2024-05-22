package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tbl_fishing_session")
data class FishingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val areaId: Area,
    val date: LocalDateTime,
    var isActive: Boolean,
    val catchId: Catch?
)