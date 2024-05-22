package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.events.FishingSessionEvent
import com.example.myapplication.repositories.IFishingSessionRepository
import com.example.myapplication.states.FishingSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LicenseViewModel(private val repository: IFishingSessionRepository) : ViewModel() {

    private val _state = MutableStateFlow(FishingSessionState())
    private val _fishingSessions: MutableStateFlow<List<FishingSession>> = MutableStateFlow(emptyList())
    val fishingSessions = _fishingSessions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFishingSessionsOrderedByDate().collect {
                sessions -> _fishingSessions.update { sessions }
            }
        }
    }

    fun onEvent(event: FishingSessionEvent) {
        when(event) {
            is FishingSessionEvent.UpsertFishingSession -> {
                viewModelScope.launch {
                    repository.upsertFishingSession(event.fishingSession)
                }
            }
        }
    }
}