package com.anyjob.domain.authorization

import com.anyjob.domain.profile.models.MapsAddress

/**
 * Данные о пользователе, необходимые для регистрации
 */
data class ProfileCreationParameters(
    val userId: String,
    val lastname: String,
    val firstname: String,
    val middlename: String?,
    val isWorker: Boolean,
    val address: MapsAddress
)
