package com.anyjob.domain

import com.anyjob.domain.authorization.useCases.CreateProfileUseCase
import com.anyjob.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import com.anyjob.domain.profile.useCases.GetAuthorizedUserUseCase
import com.anyjob.domain.search.useCases.CancelSearchUseCase
import com.anyjob.domain.search.useCases.SearchWorkerUseCase
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
        SearchWorkerUseCase(
            orderRepository = get(),
            finder = get()
        )
    }

    factory {
        CancelSearchUseCase(
            orderRepository = get(),
            finder = get()
        )
    }

    factory {
        ResendVerificationCodeUseCase(
            authorizationProvider = get()
        )
    }

    factory {
        CreateProfileUseCase(
            userRepository = get()
        )
    }

    factory {
        GetAuthorizedUserUseCase(
            authorizationProvider = get()
        )
    }
}