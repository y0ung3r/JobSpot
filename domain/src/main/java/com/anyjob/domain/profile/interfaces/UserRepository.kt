package com.anyjob.domain.profile.interfaces

import com.anyjob.domain.profile.models.User

/**
 * Определяет репозиторий пользователя
 */
interface UserRepository {
    /**
     * Обновляет информацию о пользователе
     * @param user Пользователь с обновленными свойствами
     */
    suspend fun updatePersonalInformation(user: User)
}