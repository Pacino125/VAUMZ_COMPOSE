package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.Area
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishType
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.events.FishingSessionEvent
import com.example.myapplication.repositories.IAreaRepository
import com.example.myapplication.repositories.ICatchRepository
import com.example.myapplication.repositories.IFishTypeRepository
import com.example.myapplication.repositories.IFishingSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LicenseViewModel : ViewModel(), KoinComponent {
    private val sessionRepository: IFishingSessionRepository by inject()
    private val catchRepository: ICatchRepository by inject()
    private val areaRepository: IAreaRepository by inject()
    private val fishTypeRepository: IFishTypeRepository by inject()
    private val _fishingSessions: MutableStateFlow<List<FishingSession>> = MutableStateFlow(emptyList())
    private val _mapAreasToSessions: MutableStateFlow<Map<Int, Area>> = MutableStateFlow(emptyMap())
    private val _mapCatchesToSessions: MutableStateFlow<Map<Int, Catch>> = MutableStateFlow(emptyMap())
    private val _mapCatchesToFishTypes: MutableStateFlow<Map<Int, FishType>> = MutableStateFlow(emptyMap())
    val fishingSessions = _fishingSessions.asStateFlow()
    val mapAreasToSessions = _mapAreasToSessions.asStateFlow()
    val mapCatchesToSessions = _mapCatchesToSessions.asStateFlow()
    val mapCatchesToFishTypes = _mapCatchesToFishTypes.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getFishingSessionsOrderedByDate().collect {
                sessions -> _fishingSessions.update { sessions }
                sessions.forEach {
                    loadMapForAreas(it)
                    loadMapForCatches(it)
                }
            }
        }
    }

    fun onEvent(event: FishingSessionEvent) {
        when(event) {
            is FishingSessionEvent.UpsertFishingSession -> {
                viewModelScope.launch {
                    sessionRepository.upsertFishingSession(event.fishingSession)
                }
            }
        }
    }

    private suspend fun loadMapForCatches(session: FishingSession) {
        viewModelScope.launch {
            session.catchId?.let {
                catchRepository.getCatchById(it).collect { catch ->
                    _mapCatchesToSessions.update { map ->
                        val helperMap = map.toMutableMap()
                        helperMap[session.id] = catch
                        helperMap
                    }
                }
            }
        }
    }

    private suspend fun loadMapForAreas(session: FishingSession) {
        viewModelScope.launch {
            areaRepository.getAreaById(session.areaId).collect { area ->
                _mapAreasToSessions.update { map ->
                    val helperMap = map.toMutableMap()
                    helperMap[session.id] = area
                    helperMap
                }
            }
        }
    }
}