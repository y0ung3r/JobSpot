package com.anyjob.domain.authorization.interfaces

interface AuthorizationRepository {
    fun validateConfirmationCode(code: String);
}