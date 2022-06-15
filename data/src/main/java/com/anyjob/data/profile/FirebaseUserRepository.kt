package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.data.extensions.get
import com.anyjob.data.extensions.list
import com.anyjob.domain.profile.models.User

internal class FirebaseUserRepository(private val context: FirebaseContext) : UserRepository {
    override suspend fun createProfile(parameters: ProfileCreationParameters) {
        val userId = parameters.userId
        val storeUser = context.users.get<UserEntity>(userId)?.apply {
            lastname = parameters.lastname
            firstname = parameters.firstname
            middlename = parameters.middlename
            isWorker = parameters.isWorker
            address = parameters.address
        }

        context.users.save(userId, storeUser)
    }

    override suspend fun getFreeWorkers(): List<User> {
        val freeWorkers = context.users.orderByChild("isWorker").equalTo(true).list<UserEntity>()

        return freeWorkers.map {
            User(
                id = it.id!!,
                phoneNumber = it.phoneNumber!!,
                lastname = it.lastname,
                firstname = it.firstname,
                middlename = it.middlename,
                isWorker = it.isWorker,
                address = it.address
            )
        }
    }
}