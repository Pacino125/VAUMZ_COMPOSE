package com.example.myapplication.data

import java.time.LocalDateTime

data class FishingSession(
    val guid: String,
    val areaId: Area,
    val date: LocalDateTime,
    var isActive: Boolean,
    val catchId: Catch?
)