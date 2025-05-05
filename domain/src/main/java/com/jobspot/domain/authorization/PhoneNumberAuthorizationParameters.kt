package com.jobspot.domain.authorization

/**
 * Параметры авторизации по номеру телефона
 * @param phoneNumber Номер телефона
 */
open class PhoneNumberAuthorizationParameters(
    open val phoneNumber: String
) {
    /**
     * Время ожидания (в секундах)
     */
    var timeout: Long = 60L
}