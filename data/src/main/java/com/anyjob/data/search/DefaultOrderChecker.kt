package com.anyjob.data.search

import com.anyjob.data.FirebaseContext
import com.anyjob.data.search.entities.OrderEntity
import com.anyjob.domain.search.interfaces.OrderChecker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

internal class DefaultOrderChecker(private val context: FirebaseContext) : OrderChecker {
    override fun start(orderId: String, onOrderChanged: (isFinished: Boolean, isCanceled: Boolean) -> Unit) {
        context.orders.child(orderId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue<OrderEntity>()

                if (order != null) {
                    onOrderChanged(
                        order.isFinished,
                        order.isCanceled
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}