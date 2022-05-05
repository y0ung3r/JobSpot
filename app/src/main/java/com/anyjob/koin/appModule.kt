package com.anyjob.koin

import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AuthorizationViewModel()
    }
}