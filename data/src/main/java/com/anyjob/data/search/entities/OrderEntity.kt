package com.anyjob.data.search.entities

import com.anyjob.domain.profile.models.MapsAddress
import com.google.firebase.database.Exclude
import java.io.Serializable

/**
 * Заказ
 */
internal class OrderEntity : Serializable {
    /**
     * Идентификатор заказа
     */
    @get:Exclude
    var id: String? = null

    /**
     * Идентификатор заказчика
     */
    var invokerId: String? = null

    /**
     * Географические координаты
     */
    var address: MapsAddress? = null

    /**
     * Радиус поиска (в метрах)
     */
    var searchRadius: Double? = null
}