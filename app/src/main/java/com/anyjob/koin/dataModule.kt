package com.anyjob.koin

import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationProvider
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Firebase.auth
    }

    factory<PhoneNumberAuthorizationProvider> {
        FirebasePhoneNumberAuthorizationProvider(
            firebaseProvider = get()
        )
    }
}