package com.tomiappdevelopment.imagepostscatalog.data.notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.tomiappdevelopment.imagepostscatalog.R

@SuppressLint("ServiceCast")
fun showSuccessNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, "sync_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Data Sync Successful")
        .setContentText("Your data has been synced successfully.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(1, notification)
}

fun showFailureNotification(context: Context, errorMessage: String?) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, "sync_channel")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Data Sync Failed")
        .setContentText("Error: ${errorMessage ?: "Unknown error"}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(2, notification)
}
