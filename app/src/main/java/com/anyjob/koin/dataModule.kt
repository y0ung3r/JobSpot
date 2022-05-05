package com.anyjob.koin

import com.anyjob.data.LoginDataSource
import com.anyjob.data.LoginRepository
import org.koin.dsl.module

val persistenceModule = module {
    factory {
        LoginRepository(
            dataSource = get()
        )
    }

    factory {
        LoginDataSource()
    }
}