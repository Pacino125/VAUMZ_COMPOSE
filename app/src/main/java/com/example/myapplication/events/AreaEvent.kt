package com.example.myapplication.events

sealed interface AreaEvent {
    data class SetSelectedAreaIndex(val index: Int) : AreaEvent
}