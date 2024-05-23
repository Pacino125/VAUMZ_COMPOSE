package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.entities.Area
import com.example.myapplication.events.AreaEvent
import com.example.myapplication.repositories.IAreaRepository
import com.example.myapplication.states.AreaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AreaViewModel @Inject constructor(private val repository : IAreaRepository) : ViewModel() {
    private val _state = MutableStateFlow(AreaState())
    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(emptyList())
    val areas = _areas.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAreasOrderedByName().collect {
                    areas -> _areas.update { areas }
            }
        }
    }

    fun onEvent(event: AreaEvent) {
        when(event) {
            is AreaEvent.SetSelectedAreaIndex -> TODO()
        }
    }
}