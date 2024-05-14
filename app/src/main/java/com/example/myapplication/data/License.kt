package com.example.myapplication.data

data class License(
    val guid: String,
    val licenseTypeId: LicenseType,
    val userId: String,
    val year: Int
)