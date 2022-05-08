package com.anyjob.data.authorization

import com.anyjob.domain.authorization.interfaces.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthorizationRepository(val firebaseProvider: FirebaseAuth) : AuthorizationRepository {
    override fun validateConfirmationCode(code: String) {

    }
}