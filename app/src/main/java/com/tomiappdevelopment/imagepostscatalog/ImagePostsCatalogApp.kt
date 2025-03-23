package com.tomiappdevelopment.imagepostscatalog

import android.app.Application
import com.tomiappdevelopment.imagepostscatalog.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ImagePostsCatalogApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin()
        {
            androidLogger()
            androidContext(this@ImagePostsCatalogApp)
            modules(
                appModule
            )
        }
    }

}