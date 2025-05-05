package com.jobspot

import android.app.Application
import androidx.work.Configuration
import com.jobspot.data.dataModule
import com.jobspot.data.search.GeolocationUpdaterFactory
import com.jobspot.domain.domainModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin

class AndroidApplication : Application(), Configuration.Provider {
    private lateinit var koin: Koin

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

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

    override fun getWorkManagerConfiguration(): Configuration
        = Configuration
            .Builder()
            .setWorkerFactory(GeolocationUpdaterFactory(koin))
            .build()
}