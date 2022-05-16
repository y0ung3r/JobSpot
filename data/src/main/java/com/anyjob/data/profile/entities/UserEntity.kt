package com.anyjob.data.profile.entities

import com.google.firebase.database.Exclude
import java.io.Serializable

/**
 * Сущность пользователя
 */
internal class UserEntity : Serializable {
    /**
     * Идентификатор
     */
    @get:Exclude
    lateinit var id: String

    /**
     * Номер телефона
     */
    lateinit var phoneNumber: String

    /**
     * Фамилия
     */
    lateinit var lastname: String

    /**
     * Имя
     */
    lateinit var firstname: String

    /**
     * Отчество
     */
    var middlename: String? = null

    /**
     * Определяет оказывает ли текущий пользователь услуги
     */
    var isWorker: Boolean = false
}