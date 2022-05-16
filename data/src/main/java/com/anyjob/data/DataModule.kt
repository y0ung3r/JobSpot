package com.anyjob.data

import com.anyjob.data.authorization.FirebasePhoneNumberAuthorizationProvider
import com.anyjob.data.profile.UserDataSourceImplementation
import com.anyjob.data.profile.interfaces.UserDataSource
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val dataModule = module {
    single {
        Firebase.auth
    }

    single {
        // TODO: Скрыть ссылку на базу данных
        FirebaseDatabase.getInstance("https://anyjob-fbf2f-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }

    single {
        FirebaseContext(
            database = get()
        )
    }

    single<PhoneNumberAuthorizationProvider> {
        FirebasePhoneNumberAuthorizationProvider(
            firebaseProvider = get(),
            userDataSource = get()
        )
    }

    factory<UserDataSource> {
        UserDataSourceImplementation(
            context = get()
        )
    }
}