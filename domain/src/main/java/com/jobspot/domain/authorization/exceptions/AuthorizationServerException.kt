package com.jobspot.domain.authorization.exceptions

open class AuthorizationServerException : AuthorizationException(
    "Сервер на данный момент недоступен. Попробуйте выполнить авторизацию позже"
)