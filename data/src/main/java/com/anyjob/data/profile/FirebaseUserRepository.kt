package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.get
import com.anyjob.data.extensions.save
import com.anyjob.data.extensions.asList
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.profile.models.User

internal class FirebaseUserRepository(
    private val context: FirebaseContext,
    private val authorizationProvider: PhoneNumberAuthorizationProvider
) : UserRepository {
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

    override suspend fun getAvailableWorkers(): List<User> {
        val currentUser = authorizationProvider.getAuthorizedUser() ?: return emptyList()
        val availableWorkers = context.users
            .orderByChild("worker")
            .equalTo(true)
            .asList<UserEntity>()
            .filterNot {
                it.id.equals(currentUser.id) // Спасибо Google за то, что нельзя фильтровать по нескольким полям :)
            }

        return availableWorkers.map {
            User(
                id = it.id!!,
                phoneNumber = it.phoneNumber!!,
                lastname = it.lastname,
                firstname = it.firstname,
                middlename = it.middlename,
                isWorker = it.isWorker,
                address = it.address,
                rates = it.rates
            )
        }
    }

    override suspend fun addRateToUser(userId: String, rate: Float) {
        val user = context.users.get<UserEntity>(userId)

        user?.also {
            user.rates.add(rate)
            context.users.save(userId, user)
        }
    }

    override suspend fun getUser(id: String): User? {
        val user = context.users.get<UserEntity>(id) ?: return null

        return User(
            id = user.id!!,
            phoneNumber = user.phoneNumber!!,
            lastname = user.lastname,
            firstname = user.firstname,
            middlename = user.middlename,
            isWorker = user.isWorker,
            address = user.address,
            rates = user.rates
        )
    }
}