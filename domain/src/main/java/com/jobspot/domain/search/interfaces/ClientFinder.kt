package com.jobspot.domain.search.interfaces

import com.jobspot.domain.search.models.Order

interface ClientFinder {
    fun start(onClientFound: (Order) -> Unit)
}