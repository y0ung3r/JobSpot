package com.anyjob.data.profile.entities

import com.google.firebase.database.Exclude
import java.io.Serializable

/**
 * Сущность пользователя
 * @param id Идентификатор
 */
internal class UserEntity(
    @get:Exclude
    val id: String
) : Serializable {
    /**
     * Номер телефона
     */
    var phoneNumber: String? = null

    /**
     * Фамилия
     */
    var lastname: String? = null

    /**
     * Имя
     */
    var firstname: String? = null

    /**
     * Отчество
     */
    var middlename: String? = null

    /**
     * Определяет оказывает ли текущий пользователь услуги
     */
    var isWorker: Boolean = false
}