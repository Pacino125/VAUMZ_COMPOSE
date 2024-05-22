package com.example.myapplication.states

import com.example.myapplication.entities.FishingSession

data class FishingSessionState(
    val fishingSessions: List<FishingSession> = emptyList(),
)