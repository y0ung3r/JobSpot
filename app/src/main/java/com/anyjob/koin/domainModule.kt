package com.anyjob.koin

import com.anyjob.domain.profile.useCases.SearchUserByPhoneNumberUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        SearchUserByPhoneNumberUseCase()
    }
}