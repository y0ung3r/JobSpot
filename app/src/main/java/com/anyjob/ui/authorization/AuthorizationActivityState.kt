package com.anyjob.ui.authorization

/**
 * Data validation state of the login form.
 */
data class AuthorizationActivityState(
    val phoneNumberError: Int? = null,
    val isDataValid: Boolean = false
)