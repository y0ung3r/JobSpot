package com.anyjob.data

import com.anyjob.data.entities.User
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(phoneNumber: String): Result<User> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = User(java.util.UUID.randomUUID().toString(), "Jane Doe")

            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}