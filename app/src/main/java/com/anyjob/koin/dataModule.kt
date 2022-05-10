package com.anyjob.koin

import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationSource
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Firebase.auth
    }

    factory<PhoneNumberAuthorizationSource> {
        FirebasePhoneNumberAuthorizationSource(
            firebaseProvider = get()
        )
    }
}