package com.example.myapplication.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FishingSessionWithCatch(
    @Embedded val fishingSession: FishingSession,
    @Relation(
        parentColumn = "catchId",
        entityColumn = "id"
    )
    val catch: Catch?
)