package com.jobspot.ui.explorer.profile.models

import com.jobspot.domain.search.models.Order

/**
 * Информация об авторизованном пользователе
 */
data class AuthorizedUser(
    val id: String,
    private val lastname: String?,
    private val firstname: String?,
    private val middlename: String?,
    val phoneNumber: String,
    val isWorker: Boolean,
    val currentOrder: Order? = null,
    val averageRate: Double
) {
    val fullname: String = "${lastname.orEmpty()} ${firstname.orEmpty()} ${middlename.orEmpty()}".trim()
}