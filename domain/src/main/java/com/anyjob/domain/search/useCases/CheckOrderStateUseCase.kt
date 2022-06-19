package com.anyjob.domain.search.useCases

import com.anyjob.domain.search.interfaces.OrderChecker

class CheckOrderStateUseCase(private val checker: OrderChecker) {
    fun execute(orderId: String, onOrderChanged: (isFinished: Boolean, isCanceled: Boolean) -> Unit) {
        checker.start(orderId, onOrderChanged)
    }
}