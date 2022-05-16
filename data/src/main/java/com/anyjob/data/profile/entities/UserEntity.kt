package com.anyjob.data.profile.entities

import com.google.firebase.database.Exclude
import java.io.Serializable

/**
 * Сущность пользователя
 * @param id Идентификатор
 * @param phoneNumber Номер телефона
 */
internal data class UserEntity(
    @get:Exclude
    val id: String,
    val phoneNumber: String
) : Serializable {
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
}