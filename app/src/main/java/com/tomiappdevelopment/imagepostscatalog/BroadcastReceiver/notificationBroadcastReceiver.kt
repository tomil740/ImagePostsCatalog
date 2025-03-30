package com.tomiappdevelopment.imagepostscatalog.BroadcastReceiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tomiappdevelopment.imagepostscatalog.R

class FetchPostsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getStringExtra("status") // "success" or "failure"
        val errorMessage = intent.getStringExtra("error_message") // Optional, for failure reasons

        if (status == "success") {
            showSuccessNotification(context)
        } else if (status == "failure") {
            showFailureNotification(context, errorMessage)
        }
    }

    @SuppressLint("ServiceCast")
    private fun showSuccessNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "sync_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Data Sync Successful")
            .setContentText("Your data has been synced successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showFailureNotification(context: Context, errorMessage: String?) {
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
}