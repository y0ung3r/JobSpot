package com.jobspot.domain.authorization

import com.jobspot.domain.profile.models.MapAddress

/**
 * Данные о пользователе, необходимые для регистрации
 */
data class ProfileCreationParameters(
    val userId: String,
    val lastname: String,
    val firstname: String,
    val middlename: String?,
    val isWorker: Boolean,
    val homeAddress: MapAddress,
    val professionId: String?
)
