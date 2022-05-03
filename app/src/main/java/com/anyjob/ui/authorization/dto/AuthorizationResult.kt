package com.anyjob.ui.authorization.dto

/**
 * Authentication result : success (user details) or error message.
 */
data class AuthorizationResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)