package com.anyjob.koin

import com.anyjob.domain.authorization.useCases.SendConfirmationCodeUseCase
import com.anyjob.domain.profile.useCases.SearchUserByPhoneNumberUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        SendConfirmationCodeUseCase()
    }

    factory {
        SearchUserByPhoneNumberUseCase()
    }
}