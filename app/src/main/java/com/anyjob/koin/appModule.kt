package com.anyjob.koin

import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ConfirmationCodeValidationViewModel
import com.anyjob.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.anyjob.ui.authorization.viewModels.RegistrationViewModel
import com.anyjob.ui.explorer.dashboard.viewModels.DashboardViewModel
import com.anyjob.ui.explorer.home.viewModels.HomeViewModel
import com.anyjob.ui.explorer.profile.viewModels.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AuthorizationViewModel(
            authorizationSource = get()
        )
    }

    viewModel {
        PhoneNumberEntryViewModel()
    }

    viewModel {
        ConfirmationCodeValidationViewModel()
    }

    viewModel {
        RegistrationViewModel()
    }

    viewModel {
        ProfileViewModel()
    }

    viewModel {
        DashboardViewModel()
    }

    viewModel {
        HomeViewModel()
    }
}