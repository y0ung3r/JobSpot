package com.anyjob.data

import com.google.firebase.database.DatabaseReference

internal class FirebaseContext(database: DatabaseReference) {
    val users = database.child("users")
    val orders = database.child("orders")
    val services = database.child("services")
}