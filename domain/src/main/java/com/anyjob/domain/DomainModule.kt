package com.anyjob.domain

import com.anyjob.domain.authorization.useCases.CreateProfileUseCase
import com.anyjob.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import com.anyjob.domain.profile.useCases.AddRateToUserUseCase
import com.anyjob.domain.profile.useCases.GetAuthorizedUserUseCase
import com.anyjob.domain.search.useCases.*
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
        SearchClientUseCase(
            finder = get()
        )
    }

    factory {
        SearchWorkerUseCase(
            orderRepository = get(),
            finder = get()
        )
    }

    factory {
        AcceptJobUseCase(
            authorizationProvider = get(),
            orderRepository = get()
        )
    }

    factory {
        CheckOrderStateUseCase(
            checker = get()
        )
    }

    factory {
        GetOrderExecutorUseCase(
            userRepository = get()
        )
    }

    factory {
        AddRateToUserUseCase(
            userRepository = get()
        )
    }

    factory {
        GetOrderInvokerUseCase(
            userRepository = get()
        )
    }

    factory {
        CancelOrderUseCase(
            orderRepository = get()
        )
    }

    factory {
        FinishOrderUseCase(
            orderRepository = get()
        )
    }

    factory {
        GetExecutedOrderUseCase(
            orderRepository = get()
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