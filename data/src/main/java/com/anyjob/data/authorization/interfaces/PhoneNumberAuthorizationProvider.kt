package com.anyjob.data.authorization.interfaces

import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters

/**
 * Определяет интерфейс авторизации с помощью номера телефона
 */
interface PhoneNumberAuthorizationProvider {
    /**
     * Выполняет авторизацию с помощью указанного кода
     * @param code Проверочный код
     */
    fun verifyCode(code: String, onCodeVerified: (Result<Unit>) -> Unit)

    /**
     * Отправляет код с помощью указанных параметров
     * @param authorizationParameters Параметры авторизации
     */
    fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Unit>) -> Unit)
}