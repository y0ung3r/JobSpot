package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.data.profile.interfaces.UserDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.getValue

internal class UserDataSourceImplementation(private val context: FirebaseContext) : UserDataSource {
    override fun getUser(id: String, onSuccess: (user: UserEntity?) -> Unit) {
        context.users.child(id).get().addOnSuccessListener { snapshot ->
            onSuccess.invoke(
                snapshot.getValue<UserEntity>()
            )
        }
    }

    override fun addUser(userEntity: UserEntity): Task<Void> {
        return context.users.child(userEntity.id).setValue(userEntity)
    }
}