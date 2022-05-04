package com.anyjob

import android.app.Application
import com.anyjob.koin.appModule
import com.anyjob.koin.domainModule
import com.anyjob.koin.persistenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@AndroidApplication)

            modules(
                listOf(
                    appModule,
                    domainModule,
                    persistenceModule
                )
            )
        }
    }
}