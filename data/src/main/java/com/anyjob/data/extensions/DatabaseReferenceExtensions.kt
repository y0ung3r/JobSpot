package com.anyjob.data.extensions

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.ktx.getValue

suspend inline fun <reified TEntity> DatabaseReference.get(id: String): TEntity? {
    val snapshot = child(id).get().await()
    return snapshot.getValue<TEntity>()
}

suspend inline fun DatabaseReference.save(id: String, entity: Any?) {
    child(id).setValue(entity).await()
}

suspend inline fun DatabaseReference.remove(id: String) {
    save(id, null)
}