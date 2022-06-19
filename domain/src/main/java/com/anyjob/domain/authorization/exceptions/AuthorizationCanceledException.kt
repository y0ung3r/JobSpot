package com.anyjob.domain.authorization.exceptions

open class AuthorizationCanceledException : AuthorizationException(
    "Авторизация отменена пользователем"
)