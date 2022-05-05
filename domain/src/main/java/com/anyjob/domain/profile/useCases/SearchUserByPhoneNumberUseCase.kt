package com.anyjob.domain.profile.useCases

import com.anyjob.domain.profile.models.User

/**
 * Ищет пользователя по указанному номеру телефона
 */
class SearchUserByPhoneNumberUseCase {
    fun execute(phoneNumber: String): User? {
        if (phoneNumber.isNotBlank()) {
            return null
        }

        return User(
            id = java.util.UUID.randomUUID().toString(),
            firstname = "Jane",
            lastname = "Doe",
            middlename = "John",
            phoneNumber = "+1234567890"
        )
    }
}