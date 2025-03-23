package com.tomiappdevelopment.imagepostscatalog.data.workers

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleFetchPostsWorker(theContext:Context) {
    val workManager = WorkManager.getInstance(theContext)

    val workInfos = workManager.getWorkInfosByTag("fetch_posts_worker_tag").get()

    if (workInfos.isEmpty()) {
        val initialDelay = getInitialDelayUntil2AM()

        val fetchPostsRequest = PeriodicWorkRequestBuilder<FetchPostsWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                30, TimeUnit.MINUTES
            )
            .addTag("fetch_posts_worker_tag")
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
    targetTime.set(Calendar.HOUR_OF_DAY, 2)
    targetTime.set(Calendar.MINUTE, 0)
    targetTime.set(Calendar.SECOND, 0)
    targetTime.set(Calendar.MILLISECOND, 0)

    // If current time is already past 2 AM, schedule for the next day
    if (currentTime.after(targetTime)) {
        targetTime.add(Calendar.DAY_OF_YEAR, 1) // Set to next day's 2 AM
    }

    // Return the delay until the target time
    return targetTime.timeInMillis - currentTime.timeInMillis
}