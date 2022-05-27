package com.anyjob.ui.explorer.profile.models

/**
 * Информация об авторизованном пользователе
 */
data class AuthorizedUser(
    val id: String,
    private val lastname: String?,
    private val firstname: String?,
    private val middlename: String?,
    val phoneNumber: String
) {
    val fullname: String = "${lastname.orEmpty()} ${firstname.orEmpty()} ${middlename.orEmpty()}".trim()
}