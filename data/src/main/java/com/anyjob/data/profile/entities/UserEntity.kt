package com.anyjob.data.profile.entities

import com.anyjob.domain.profile.models.MapsAddress
import java.io.Serializable

/**
 * Сущность пользователя
 */
internal class UserEntity : Serializable {
    /**
     * Идентификатор пользователя
     */
    var id: String? = null

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

    /**
     * Домашний адрес
     */
    var address: MapsAddress? = null

    /**
     * Оценки пользователя
     */
    var rates: ArrayList<Float> = arrayListOf(5.0f)
}