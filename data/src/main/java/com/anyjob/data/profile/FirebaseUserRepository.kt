package com.anyjob.data.profile

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.data.extensions.get
import com.anyjob.data.extensions.notEqualTo
import com.anyjob.data.extensions.toList
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.profile.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FirebaseUserRepository(
    private val context: FirebaseContext,
    private val firebaseProvider: FirebaseAuth
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
        val currentUser = firebaseProvider.currentUser ?: return emptyList()
        val availableWorkers = context.users
            .orderByChild("worker")
            .equalTo(true)
            .toList<UserEntity>()
            .filterNot {
                it.id.equals(currentUser.uid) // Спасибо Google за то, что нельзя фильтровать по нескольким полям :)
            }

        return availableWorkers.map {
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