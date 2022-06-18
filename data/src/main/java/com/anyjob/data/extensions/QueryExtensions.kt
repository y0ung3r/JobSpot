package com.anyjob.data.extensions

import com.google.firebase.database.Query
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

internal suspend inline fun <reified TEntity> Query.toList(): List<TEntity> {
    val querySnapshot = get().await()
    val entities = ArrayList<TEntity>()

    querySnapshot.children.forEach { entitySnapshot ->
        val entity = entitySnapshot.getValue<TEntity>()

        if (entity != null) {
            entities.add(entity)
        }
    }

    return entities
}

fun Query.notEqualTo(value: String): Query {
    return startAfter(value).endBefore(value)
}