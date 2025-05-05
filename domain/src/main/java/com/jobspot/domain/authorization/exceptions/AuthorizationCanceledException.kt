package com.jobspot.domain.authorization.exceptions

open class AuthorizationCanceledException : AuthorizationException(
    "Авторизация отменена пользователем"
)