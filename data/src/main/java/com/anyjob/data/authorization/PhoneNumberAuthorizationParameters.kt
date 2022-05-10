package com.anyjob.data.authorization

/**
 * Параметры авторизации по номеру телефона
 * @param phoneNumber Номер телефона
 */
open class PhoneNumberAuthorizationParameters(
    open val phoneNumber: String
)