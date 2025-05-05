package com.jobspot.domain.search.useCases

import com.jobspot.domain.search.interfaces.ClientFinder
import com.jobspot.domain.search.models.Order

class SearchClientUseCase(private val finder: ClientFinder) {
    fun execute(onClientFound: (Order) -> Unit) {
        finder.start(onClientFound)
    }
}