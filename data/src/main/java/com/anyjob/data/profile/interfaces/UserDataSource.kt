package com.anyjob.data.profile.interfaces

import com.anyjob.data.profile.entities.UserEntity
import com.google.android.gms.tasks.Task

/**
 * Определяет источник данных для пользовательских данных
 */
internal interface UserDataSource {
    /**
     * Возвращает пользователя по идентификатору
     * @param id Идентификатор
     * @param onSuccess Событие, вызываемое в случае успешного выполнения запроса
     */
    fun getUser(id: String, onSuccess: (user: UserEntity?) -> Unit)

    /**
     * Добавляет пользователя
     * @param userEntity Пользователь
     */
    fun addUser(userEntity: UserEntity) : Task<Void>
}