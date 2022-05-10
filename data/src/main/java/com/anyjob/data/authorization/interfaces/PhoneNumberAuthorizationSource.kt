package com.anyjob.data.authorization.interfaces

import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.data.authorization.exceptions.AuthorizationException

/**
 * Определяет интерфейс авторизации с помощью номера телефона
 */
interface PhoneNumberAuthorizationSource {
    /**
     * Выполняет авторизацию с помощью указанного кода
     * @param code Проверочный код
     */
    fun verifyCode(code: String)

    /**
     * Отправляет код с помощью указанных параметров
     * @param authorizationParameters Параметры авторизации
     */
    fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters)

    /**
     * Задает слушателя, который будет вызван после успешной отправки проверочного кода
     */
    fun setOnCodeSendingStateListener(listener: OnCodeSendingStateListener): PhoneNumberAuthorizationSource;

    /**
     * Определяет интерфейс для прослушивания события об отправке проверочного кода
     */
    interface OnCodeSendingStateListener {
        /**
         * Вызывается при успешной отправке проверочного кода
         */
        fun onSuccess()

        /**
         * Вызывается при неудачной попытке отправки проверочного кода
         */
        fun onFailed(exception: AuthorizationException)
    }
}