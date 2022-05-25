package com.anyjob.domain.profile.interfaces

import com.anyjob.domain.authorization.ProfileCreationParameters

/**
 * Определяет репозиторий пользователя
 */
interface UserRepository {
    /**
     * Создает личный кабинет для пользователя
     * @param parameters Личные данные пользователя
     */
    suspend fun createProfile(parameters: ProfileCreationParameters)
}