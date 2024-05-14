package com.example.myapplication.data

data class User(
    val guid: String,
    val email: String,
    val name: String,
    val fullname: String,
    val password: String,
    val organizationGuid: String?,
    val child: Boolean,
    val birth: String,
    val address: String,
    val memberYear: String?,
    val number: String?
)