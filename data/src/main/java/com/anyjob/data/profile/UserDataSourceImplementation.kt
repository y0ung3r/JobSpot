package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.data.profile.interfaces.UserDataSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.ktx.getValue
import java.lang.Exception

internal class UserDataSourceImplementation(private val context: FirebaseContext) : UserDataSource {
    override fun getUser(id: String) : Result<UserEntity?> {
        val user = try {
            Result.success(
                context.users.child(id).get().await().getValue<UserEntity>()
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }

        return user
    }

    override fun addUser(userEntity: UserEntity): Task<Void> {
        return context.users.child(userEntity.id).setValue(userEntity)
    }
}