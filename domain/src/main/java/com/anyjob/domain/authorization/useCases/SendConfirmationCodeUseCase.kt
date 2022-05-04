package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.profile.models.PhoneNumber

/**
 * Отправляет код подтверждения пользователю
 */
class SendConfirmationCodeUseCase {
    fun execute(phoneNumber: PhoneNumber) {
        // TODO: отправить SMS пользователю по указанному номеру
    }
}