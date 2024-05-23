package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "tbl_fishing_session",
    foreignKeys = [ForeignKey(
        entity = Catch::class,
        parentColumns = ["id"],
        childColumns = ["catchId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["catchId"])]
)
data class FishingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val areaId: Area,
    val date: LocalDateTime,
    var isActive: Boolean,
    var catchId: Int?
)