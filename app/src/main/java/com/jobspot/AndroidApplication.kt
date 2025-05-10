package com.jobspot

import android.app.Application
import com.jobspot.data.dataModule
import com.jobspot.domain.domainModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin

class AndroidApplication : Application() {
    private lateinit var koin: Koin

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        koin = startKoin {
            //androidLogger(level = Level.DEBUG)
            androidContext(this@AndroidApplication)

            modules(
                listOf(
                    appModule,
                    dataModule,
                    domainModule
                )
            )
        }.koin
    }
}