package com.jobspot.data.profile

import com.jobspot.data.FirebaseContext
import com.jobspot.data.extensions.get
import com.jobspot.data.extensions.save
import com.jobspot.data.extensions.asList
import com.jobspot.data.profile.entities.UserEntity
import com.jobspot.domain.authorization.ProfileCreationParameters
import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.MapAddress
import com.jobspot.domain.profile.models.User

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
            homeAddress = parameters.homeAddress
            professionId = parameters.professionId
            encodedInn = parameters.encodedInn
            encodedDiploma = parameters.encodedDiploma
            encodedEmploymentHistoryBook = parameters.encodedEmploymentHistoryBook
            isDocumentsVerified = false
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
                homeAddress = it.homeAddress,
                geolocation = it.geolocation,
                professionId = it.professionId,
                isDocumentsVerified = it.isDocumentsVerified,
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

    override suspend fun updateGeolocation(userId: String, geolocation: MapAddress) {
        val user = context.users.get<UserEntity>(userId)

        user?.also {
            user.geolocation = geolocation
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
            homeAddress = user.homeAddress,
            geolocation = user.geolocation,
            professionId = user.professionId,
            isDocumentsVerified = user.isDocumentsVerified,
            rates = user.rates
        )
    }
}