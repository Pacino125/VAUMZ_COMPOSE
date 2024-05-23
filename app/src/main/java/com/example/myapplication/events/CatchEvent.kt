package com.example.myapplication.events

import com.example.myapplication.entities.Catch

sealed interface CatchEvent {
    data class AddCatchToFishingSession(val catch: Catch, val fishingSessionId: Int): CatchEvent
    data class SetCount(val count: Int) : CatchEvent
    data class SetLength(val length: Int) : CatchEvent
    data class SetWeight(val weight: Double) : CatchEvent
    data class SetFishingSession(val session: Int) : CatchEvent
}