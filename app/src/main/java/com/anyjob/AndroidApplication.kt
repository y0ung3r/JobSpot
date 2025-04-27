package com.anyjob

import android.app.Application
import androidx.work.Configuration
import com.anyjob.data.dataModule
import com.anyjob.data.search.GeolocationUpdaterFactory
import com.anyjob.domain.domainModule
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