package com.example.myapplication.data

data class Area(
    val guid: String,
    val name: String,
    val areaId: String,
    val areaTypeId: AreaType?,
    val organizationId: String?,
    val coordinatesId: String?,
    val chap: Boolean?
)