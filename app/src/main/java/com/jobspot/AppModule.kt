package com.jobspot

import com.jobspot.ui.authorization.viewModels.AuthorizationViewModel
import com.jobspot.ui.authorization.viewModels.ConfirmationCodeVerifyingViewModel
import com.jobspot.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.jobspot.ui.authorization.viewModels.ProfileCreationViewModel
import com.jobspot.ui.explorer.search.viewModels.SearchViewModel
import com.jobspot.ui.explorer.viewModels.ExplorerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AuthorizationViewModel(
            sendVerificationCodeUseCase = get(),
            verifyCodeUseCase = get(),
            resendVerificationCodeUseCase = get(),
            getAuthorizedUserUseCase = get()
        )
    }

    viewModel {
        PhoneNumberEntryViewModel()
    }

    viewModel {
        ConfirmationCodeVerifyingViewModel()
    }

    viewModel {
        ProfileCreationViewModel(
            createProfileUseCase = get(),
            getServicesUseCase = get()
        )
    }

    viewModel {
        ExplorerViewModel(
            getAuthorizedUserUseCase = get(),
            searchClientUseCase = get(),
            acceptJobUseCase = get(),
            getExecutedOrderUseCase = get(),
            cancelOrderUseCase = get(),
            getOrderExecutorUseCase = get(),
            getOrderInvokerUseCase = get(),
            checkOrderStateUseCase = get(),
            finishOrderUseCase = get(),
            addRateToUserUseCase = get(),
            logoutUseCase = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchWorkerUseCase = get(),
            cancelSearchUseCase = get(),
            getServicesUseCase = get()
        )
    }
}