package com.hazratbilal.notecraft.model

data class UserRequest (
    val full_name: String,
    val email: String,
    val gender: String,
    val password: String,
    val new_password: String
)