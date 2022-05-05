package com.anyjob.data.profile.entities

/**
 * Пользователь
 */
data class UserEntity(
    val id: String,
    var firstname: String,
    var lastname: String,
    var middlename: String,
    var phoneNumber: String
)