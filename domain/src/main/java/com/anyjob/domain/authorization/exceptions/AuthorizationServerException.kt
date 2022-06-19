package com.anyjob.domain.authorization.exceptions

open class AuthorizationServerException : AuthorizationException(
    "Сервер на данный момент недоступен. Попробуйте выполнить авторизацию позже"
)