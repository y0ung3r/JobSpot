package com.jobspot.domain.authorization.interfaces

import com.jobspot.domain.authorization.PhoneNumberAuthorizationParameters
import com.jobspot.domain.profile.models.User

/**
 * Определяет интерфейс авторизации с помощью номера телефона
 */
interface PhoneNumberAuthorizationProvider {
    /**
     * Выполняет авторизацию с помощью указанного кода
     * @param code Проверочный код
     */
    suspend fun verifyCode(code: String)

    /**
     * Отправляет код с помощью указанных параметров
     * @param authorizationParameters Параметры авторизации
     */
    fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Unit>) -> Unit)

    /**
     * Повторно отправляет проверочный код, используя сохраненные параметры авторизации
     */
    fun resendCode(onCodeResent: (Result<Unit>) -> Unit)

    /**
     * Возвращает авторизованного пользователя
     */
    suspend fun getAuthorizedUser() : User?

    /**
     * Выполняет выход из системы для текущего пользователя
     */
    fun logout()
}