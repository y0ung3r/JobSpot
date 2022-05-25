package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.profile.interfaces.UserRepository

internal class FirebaseUserRepository(private val context: FirebaseContext) : UserRepository {
    override suspend fun createProfile(parameters: ProfileCreationParameters) {
        val userEntity = UserEntity().apply {
            lastname = parameters.lastname
            firstname = parameters.firstname
            middlename = parameters.middlename
            isWorker = parameters.isWorker
        }

        context.users.save(parameters.userId, userEntity)
    }
}