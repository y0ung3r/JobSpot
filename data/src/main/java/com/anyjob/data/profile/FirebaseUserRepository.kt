package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.profile.models.User

internal class FirebaseUserRepository(private val context: FirebaseContext) : UserRepository {
    override suspend fun updatePersonalInformation(user: User) {
        val userEntity = UserEntity().apply {
            id = user.id
            phoneNumber = user.phoneNumber
            lastname = user.lastname
            firstname = user.firstname
            middlename = user.middlename
            isWorker = user.isWorker
        }

        context.users.save(user.id, userEntity)
    }
}