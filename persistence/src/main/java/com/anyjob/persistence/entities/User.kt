package com.anyjob.persistence.entities

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val id: String,
    val displayName: String
)