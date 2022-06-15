package com.anyjob.data.extensions

import com.google.firebase.database.Query
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

internal suspend inline fun <reified TEntity> Query.list(): List<TEntity> {
    val snapshot = get().await()
    return snapshot.getValue<List<TEntity>>() ?: emptyList()
}