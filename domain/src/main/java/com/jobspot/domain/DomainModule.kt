package com.jobspot.domain

import com.jobspot.domain.authorization.useCases.CreateProfileUseCase
import com.jobspot.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.jobspot.domain.authorization.useCases.SendVerificationCodeUseCase
import com.jobspot.domain.authorization.useCases.VerifyCodeUseCase
import com.jobspot.domain.profile.useCases.AddRateToUserUseCase
import com.jobspot.domain.profile.useCases.GetAuthorizedUserUseCase
import com.jobspot.domain.profile.useCases.LogoutUseCase
import com.jobspot.domain.profile.useCases.StartVerificationListenerUseCase
import com.jobspot.domain.search.useCases.*
import com.jobspot.domain.services.useCases.GetServicesUseCase
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
        StartVerificationListenerUseCase(
            verificationListener = get()
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

    factory {
        GetServicesUseCase(
            serviceRepository = get()
        )
    }

    factory {
        LogoutUseCase(
            authorizationProvider = get()
        )
    }
}