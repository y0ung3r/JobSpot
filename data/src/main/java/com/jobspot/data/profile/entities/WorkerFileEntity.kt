package com.jobspot.data.profile.entities

import java.io.Serializable

internal class WorkerFileEntity : Serializable {
    /**
     * Идентификатор заказчика
     */
    var workerId: String? = null

    /**
     * Закодированный файл, содержащий ИНН
     */
    var encodedInn: String? = null

    /**
     * Закодированный файл, содержащий диплом
     */
    var encodedDiploma: String? = null

    /**
     * Закодированный файл, содержащий трудовую книгу
     */
    var encodedEmploymentHistoryBook: String? = null
}
