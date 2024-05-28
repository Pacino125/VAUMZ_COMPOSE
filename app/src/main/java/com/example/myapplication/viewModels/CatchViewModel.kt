package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.Catch
import com.example.myapplication.entities.FishType
import com.example.myapplication.entities.FishingSession
import com.example.myapplication.repositories.ICatchRepository
import com.example.myapplication.repositories.IFishTypeRepository
import com.example.myapplication.repositories.IFishingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CatchViewModel : ViewModel(), KoinComponent {
    private val fishTypeRepository: IFishTypeRepository by inject()
    private val fishingSessionRepository: IFishingSessionRepository by inject()
    private val catchRepository: ICatchRepository by inject()
    private val _fishTypes: MutableStateFlow<List<FishType>> = MutableStateFlow(emptyList())
    val fishTypes = _fishTypes.asStateFlow()

    init {
        viewModelScope.launch {
            fishTypeRepository.getFishTypes().collect {
                    fishTypes -> _fishTypes.update { fishTypes }
            }
        }
    }

    suspend fun insertNewCatch(catch: Catch) {
        catchRepository.insertCatch(catch)
    }

    suspend fun getHighestId(): Flow<Int?> {
        return catchRepository.getHighestId()
    }

    suspend fun getActiveSession(): Flow<FishingSession> {
        return fishingSessionRepository.getActiveSession()
    }

    suspend fun updateCurrentSession(fishingSession: FishingSession) {
        viewModelScope.launch {
            fishingSessionRepository.upsertFishingSession(fishingSession)
        }
    }
}