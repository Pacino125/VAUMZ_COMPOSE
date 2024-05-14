package com.example.myapplication.data

data class LicenseType(
    val guid: String,
    val type: String,
    val description: String?,
    val priceForAdult: Int,
    val priceForChild: Int?
)