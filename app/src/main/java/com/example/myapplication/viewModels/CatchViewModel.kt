package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.FishType
import com.example.myapplication.events.CatchEvent
import com.example.myapplication.repositories.IFishingSessionsAndCatchesRepository
import com.example.myapplication.states.CatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatchViewModel @Inject constructor(private val repository: IFishingSessionsAndCatchesRepository): ViewModel() {
    private val _state = MutableStateFlow(CatchState())
    private val _fishTypes: MutableStateFlow<List<FishType>> = MutableStateFlow(emptyList())
    val fishTypes = _fishTypes.asStateFlow()

    fun onEvent(event: CatchEvent) {
        when(event) {
            is CatchEvent.SetCount -> {
                _state.update { it.copy(
                    count = event.count
                )}
            }
            is CatchEvent.SetLength -> {
                _state.update {
                    it.copy(
                        length = event.length
                    )
                }
            }
            is CatchEvent.SetWeight -> {
                _state.update {
                    it.copy(
                        weight = event.weight
                    )
                }
            }

            is CatchEvent.AddCatchToFishingSession -> {
                viewModelScope.launch {
                    repository.addCatchToFishingSession(event.catch, event.fishingSessionId)
                }
            }

            is CatchEvent.SetFishingSession -> {
                _state.update {
                    it.copy(
                        fishingSessionId = event.session
                    )
                }
            }
        }
    }
}