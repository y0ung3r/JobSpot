package com.anyjob.domain

import com.anyjob.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        SendVerificationCodeUseCase(
            authorizationProvider = get()
        )
    }

    factory {
        VerifyCodeUseCase(
            authorizationProvider = get()
        )
    }

    factory {
        ResendVerificationCodeUseCase(
            authorizationProvider = get()
        )
    }
}