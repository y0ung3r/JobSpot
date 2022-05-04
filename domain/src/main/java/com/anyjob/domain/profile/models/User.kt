package com.anyjob.domain.profile.models

/**
 * Пользователь
 */
data class User(
    val id: String,
    var firstname: String,
    var lastname: String,
    var middlename: String,
    var phoneNumber: PhoneNumber
)