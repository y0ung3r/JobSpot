package com.anyjob.data.authorization.exceptions

open class AuthorizationServerException() : Exception(
    "Сервер на данный момент недоступен. Попробуйте выполнить авторизацию позже"
)