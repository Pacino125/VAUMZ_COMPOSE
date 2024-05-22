package com.example.myapplication.events

import com.example.myapplication.entities.FishingSession

sealed interface FishingSessionEvent {
    data class UpsertFishingSession(val fishingSession: FishingSession) : FishingSessionEvent
}