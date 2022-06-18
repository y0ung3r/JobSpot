package com.anyjob.data

import com.anyjob.data.authorization.FirebasePhoneNumberAuthorizationProvider
import com.anyjob.data.profile.FirebaseUserRepository
import com.anyjob.data.search.DefaultWorkerFinder
import com.anyjob.data.search.FirebaseOrderRepository
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.search.interfaces.WorkerFinder
import com.anyjob.domain.search.interfaces.OrderRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val dataModule = module {
    single {
        Firebase.auth
    }

    single {
        FirebaseDatabase.getInstance().reference
    }

    single {
        FirebaseContext(
            database = get()
        )
    }

    single<PhoneNumberAuthorizationProvider> {
        FirebasePhoneNumberAuthorizationProvider(
            firebaseProvider = get(),
            context = get()
        )
    }

    single<WorkerFinder> {
        DefaultWorkerFinder(
            userRepository = get()
        )
    }

    factory<UserRepository> {
        FirebaseUserRepository(
            context = get(),
            firebaseProvider = get()
        )
    }

    factory<OrderRepository> {
        FirebaseOrderRepository(
            context = get()
        )
    }
}