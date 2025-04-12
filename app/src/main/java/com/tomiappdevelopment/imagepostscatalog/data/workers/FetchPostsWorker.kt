package com.tomiappdevelopment.imagepostscatalog.data.workers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.tomiappdevelopment.imagepostscatalog.data.maper.toWorkerResult
import com.tomiappdevelopment.imagepostscatalog.data.notifications.showFailureNotification
import com.tomiappdevelopment.imagepostscatalog.data.notifications.showSuccessNotification
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import com.tomiappdevelopment.imagepostscatalog.domain.util.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FetchPostsWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    //private val postRepository: PostRepository
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val postRepository: PostRepository by inject()


    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            if(runAttemptCount >= 5) {
                 Result.failure()
            }
            try {
                // flag -1 as a worker to init the data
                val result = postRepository.fetchAndUpdatePosts(-1)

                // Handle the result and map it to ListenableWorker.Result
                when (result) {

                    is com.tomiappdevelopment.imagepostscatalog.domain.util.Result.Success ->{
                        showSuccessNotification(applicationContext)
                        Result.success()
                    }

                    is com.tomiappdevelopment.imagepostscatalog.domain.util.Result.Error -> {
                        showFailureNotification(applicationContext,result.error.toString())
                        result.error.toWorkerResult()
                    }
                }
            } catch (e: Exception) {
                // In case of exception, retry the work
                ListenableWorker.Result.retry()
            }
        }
    }
    private fun sendBroadcast(status: String, errorMessage: String? = null) {
        Log.i("not","notification Sent... ")
        val intent = Intent("com.tomiappdevelopment.imagepostscatalog.FETCH_POSTS_COMPLETE")
        intent.putExtra("status", status)
        intent.putExtra("error_message", errorMessage)
        applicationContext.sendBroadcast(intent)
    }
}