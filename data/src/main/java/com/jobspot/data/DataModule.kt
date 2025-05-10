package com.jobspot.data

import com.jobspot.data.authorization.FirebasePhoneNumberAuthorizationProvider
import com.jobspot.data.profile.FirebaseUserRepository
import com.jobspot.data.search.DefaultClientFinder
import com.jobspot.data.search.DefaultOrderChecker
import com.jobspot.data.search.DefaultWorkerFinder
import com.jobspot.data.search.FirebaseOrderRepository
import com.jobspot.data.services.FirebaseServiceRepository
import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.search.interfaces.ClientFinder
import com.jobspot.domain.search.interfaces.OrderChecker
import com.jobspot.domain.search.interfaces.WorkerFinder
import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.services.interfaces.ServiceRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jobspot.data.profile.DefaultVerificationListener
import com.jobspot.domain.profile.interfaces.VerificationListener
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

    single<OrderChecker> {
        DefaultOrderChecker(
            context = get()
        )
    }

    single<WorkerFinder> {
        DefaultWorkerFinder(
            userRepository = get(),
            orderRepository = get()
        )
    }

    single<ClientFinder> {
        DefaultClientFinder(
            orderRepository = get(),
            authorizationProvider = get()
        )
    }

    single<VerificationListener> {
        DefaultVerificationListener(
            authorizationProvider = get()
        )
    }

    factory<UserRepository> {
        FirebaseUserRepository(
            context = get(),
            authorizationProvider = get()
        )
    }

    factory<OrderRepository> {
        FirebaseOrderRepository(
            context = get(),
            authorizationProvider = get()
        )
    }

    factory<ServiceRepository> {
        FirebaseServiceRepository(
            context = get()
        )
    }
}