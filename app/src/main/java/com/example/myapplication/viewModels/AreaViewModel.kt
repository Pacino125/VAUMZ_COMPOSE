package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.repositories.IAreaRepository
import com.example.myapplication.repositories.IFishingSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AreaViewModel : ViewModel(), KoinComponent {
    private val areaRepository: IAreaRepository by inject()
    private val sessionRepository: IFishingSessionRepository by inject()
    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(emptyList())
    val areas = _areas.asStateFlow()

    init {
        viewModelScope.launch {
            areaRepository.getAreasOrderedByName().collect {
                    areas -> _areas.update { areas }
            }
        }
    }

    suspend fun insertNewFishingSession(fishingSession: FishingSession) {
        sessionRepository.upsertFishingSession(fishingSession)
    }
}