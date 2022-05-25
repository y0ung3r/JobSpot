package com.anyjob.domain.authorization

/**
 * Данные о пользователе, необходимые для регистрации
 */
data class ProfileCreationParameters(
    val userId: String,
    val lastname: String,
    val firstname: String,
    val middlename: String?,
    val isWorker: Boolean
)
