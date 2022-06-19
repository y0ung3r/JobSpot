package com.anyjob

import android.app.Application
import com.anyjob.data.dataModule
import com.anyjob.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            //androidLogger(level = Level.DEBUG)
            androidContext(this@AndroidApplication)

            modules(
                listOf(
                    appModule,
                    dataModule,
                    domainModule
                )
            )
        }
    }
}