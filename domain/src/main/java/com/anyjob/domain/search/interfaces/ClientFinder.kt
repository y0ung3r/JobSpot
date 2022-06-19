package com.anyjob.domain.search.interfaces

import com.anyjob.domain.search.models.Order

interface ClientFinder {
    fun start(onClientFound: (Order) -> Unit)
}