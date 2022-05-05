package com.anyjob.data.entities

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val id: String,
    val displayName: String
)