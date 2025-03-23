package com.tomiappdevelopment.imagepostscatalog

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tomiappdevelopment.imagepostscatalog.data.workers.FetchPostsWorker
import com.tomiappdevelopment.imagepostscatalog.data.workers.scheduleFetchPostsWorker
import com.tomiappdevelopment.imagepostscatalog.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class ImagePostsCatalogApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // Schedule the worker when the app starts
        scheduleFetchPostsWorker(this)

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