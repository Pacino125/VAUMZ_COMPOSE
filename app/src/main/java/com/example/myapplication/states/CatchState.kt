package com.example.myapplication.states

import com.example.myapplication.entities.FishType

data class CatchState(
    val fishType: FishType? = null,
    val count: Int = 1,
    val length: Int = 0,
    val weight: Double = 0.0,
    val fishingSessionId: Int = 0
)