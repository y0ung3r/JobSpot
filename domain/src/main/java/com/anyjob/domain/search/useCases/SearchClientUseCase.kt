package com.anyjob.domain.search.useCases

import com.anyjob.domain.search.interfaces.ClientFinder
import com.anyjob.domain.search.models.Order

class SearchClientUseCase(private val finder: ClientFinder) {
    fun execute(onClientFound: (Order) -> Unit) {
        finder.start(onClientFound)
    }
}