package com.anyjob.data.authorization.firebase

import android.app.Activity
import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters

/**
 * Параметры авторизации по номеру телефона через Firebase
 * @param phoneNumber Номер телефона
 * @param activity Activity, выполняющий авторизацию через Firebase
 */
class FirebasePhoneNumberAuthorizationParameters(override val phoneNumber: String, val activity: Activity) : PhoneNumberAuthorizationParameters(phoneNumber) {
    /**
     * Время ожидания (в секундах)
     */
    var timeout: Long = 60L
}