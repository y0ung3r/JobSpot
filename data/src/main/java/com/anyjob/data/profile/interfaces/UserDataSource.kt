package com.anyjob.data.profile.interfaces

import com.anyjob.data.profile.entities.UserEntity

/**
 * Определяет источник данных для пользовательских данных
 */
internal interface UserDataSource {
    /**
     * Возвращает пользователя по идентификатору
     * @param id Идентификатор
     */
    suspend fun getUser(id: String) : UserEntity?

    /**
     * Добавляет пользователя
     * @param userEntity Пользователь
     */
    fun addUser(userEntity: UserEntity)
}