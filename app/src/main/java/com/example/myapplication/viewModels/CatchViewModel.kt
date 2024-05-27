package com.example.myapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.events.CatchEvent
import com.example.myapplication.repositories.ICatchRepository
import com.example.myapplication.states.CatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatchViewModel(private val repository: ICatchRepository): ViewModel() {
    private val _state = MutableStateFlow(CatchState())

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

            is CatchEvent.InsertCatch -> {
                viewModelScope.launch {
                    repository.insertCatch(event.catch)
                }
            }
        }
    }
}