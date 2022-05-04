package com.anyjob.domain.profile.useCases

import com.anyjob.domain.profile.models.PhoneNumber
import com.anyjob.domain.profile.models.User

/**
 * Ищет пользователя по указанному номеру телефона
 */
class SearchUserByPhoneNumberUseCase {
    fun execute(phoneNumber: PhoneNumber): User? {
        if (phoneNumber.value.isNotBlank()) {
            return null
        }

        return User(
            id = java.util.UUID.randomUUID().toString(),
            firstname = "Jane",
            lastname = "Doe",
            middlename = "John",
            phoneNumber = PhoneNumber("+1234567890")
        )
    }
}