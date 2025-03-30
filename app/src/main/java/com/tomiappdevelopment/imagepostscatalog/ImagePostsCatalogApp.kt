package com.tomiappdevelopment.imagepostscatalog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tomiappdevelopment.imagepostscatalog.BroadcastReceiver.FetchPostsBroadcastReceiver
import com.tomiappdevelopment.imagepostscatalog.data.workers.FetchPostsWorker
import com.tomiappdevelopment.imagepostscatalog.data.workers.scheduleFetchPostsWorker
import com.tomiappdevelopment.imagepostscatalog.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class ImagePostsCatalogApp: Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
       // registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        createNotificationChannel(this)
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

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sync_channel", // Channel ID
                "Data Sync Notifications", // Channel name
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for data sync process"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}