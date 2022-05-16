package com.anyjob.data

import com.google.firebase.database.DatabaseReference

internal class FirebaseContext(private val database: DatabaseReference) {
    val users = database.child("users")
}