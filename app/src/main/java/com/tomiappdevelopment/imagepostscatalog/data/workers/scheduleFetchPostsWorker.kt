package com.tomiappdevelopment.imagepostscatalog.data.workers

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleFetchPostsWorker(theContext: Context) {
    val workManager = WorkManager.getInstance(theContext)

    val workInfos = workManager.getWorkInfosByTag("fetch_posts_worker_tag").get()

    if (workInfos.isEmpty()) {
        val initialDelay = getInitialDelayUntil2AM()

        // Define the network constraint (only runs if there's an internet connection)
        val networkConstraint = NetworkConnectionHelper.createNetworkConstraint()

        val fetchPostsRequest = PeriodicWorkRequestBuilder<FetchPostsWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL, // Exponential backoff
                10, TimeUnit.MINUTES // Initial delay between retries is 10 minutes, but it will grow exponentially
            )
            .addTag("fetch_posts_worker_tag")
            .setConstraints(networkConstraint) // Set network constraint
            .build()

        workManager.enqueue(fetchPostsRequest)
    } else {
        Log.d("FetchPostsWorker", "Worker is already scheduled")
    }
}


fun getInitialDelayUntil2AM(): Long {
    val currentTime = Calendar.getInstance()
    val targetTime = Calendar.getInstance()

    // Set target time to 2 AM
    targetTime.set(Calendar.HOUR_OF_DAY, 13)
    targetTime.set(Calendar.MINUTE, 19)
    targetTime.set(Calendar.SECOND, 0)
    targetTime.set(Calendar.MILLISECOND, 0)

    // If current time is already past 2 AM, schedule for the next day
    if (currentTime.after(targetTime)) {
        targetTime.add(Calendar.DAY_OF_YEAR, 1) // Set to next day's 2 AM
    }

    // Return the delay until the target time
    return targetTime.timeInMillis - currentTime.timeInMillis
}

object NetworkConnectionHelper {
    // Create the network constraint to ensure internet connection
    fun createNetworkConstraint(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}