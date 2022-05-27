package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.data.extensions.get

internal class FirebaseUserRepository(private val context: FirebaseContext) : UserRepository {
    override suspend fun createProfile(parameters: ProfileCreationParameters) {
        val userId = parameters.userId
        val storeUser = context.users.get<UserEntity>(userId)?.apply {
            lastname = parameters.lastname
            firstname = parameters.firstname
            middlename = parameters.middlename
            isWorker = parameters.isWorker
        }

        context.users.save(userId, storeUser)
    }
}