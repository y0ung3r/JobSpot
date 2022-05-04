package com.anyjob.koin

import com.anyjob.persistence.LoginDataSource
import com.anyjob.persistence.LoginRepository
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