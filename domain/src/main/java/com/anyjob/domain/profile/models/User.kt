package com.anyjob.domain.profile.models

/**
 * Пользователь
 * @param id Идентификатор пользователя
 * @param phoneNumber Номер телефона
 * @param lastname Фамилия
 * @param firstname Имя
 * @param middlename Отчество
 * @param professionId Идентификатор рода занятий
 * @param isWorker Определяет оказывает ли текущий пользователь услуги
 */
data class User(
    val id: String,
    val phoneNumber: String,
    val lastname: String?,
    val firstname: String?,
    val middlename: String?,
    val isWorker: Boolean,
    val homeAddress: MapAddress?,
    val geolocation: MapAddress?,
    val professionId: String?,
    val rates: List<Float> = listOf(5.0f)
) {
    val averageRate = rates.average()
}