package com.anyjob.koin

import com.anyjob.data.authorization.FirebaseAuthorizationRepository
import com.anyjob.domain.authorization.interfaces.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val persistenceModule = module {
    single {
        FirebaseAuth.getInstance()
    }

    factory<AuthorizationRepository> {
        FirebaseAuthorizationRepository(
            firebaseProvider = get()
        )
    }
}