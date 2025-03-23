package com.tomiappdevelopment.imagepostscatalog.data.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.tomiappdevelopment.imagepostscatalog.domain.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchPostsWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val postRepository: PostRepository // Inject repository (Koin, Hilt, etc.)
) : CoroutineWorker(appContext, workerParams) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): ListenableWorker.Result {
        return withContext(Dispatchers.IO) {
            try {
                // Perform the fetch operation and return the appropriate result
                val result = postRepository.fetchAndUpdatePosts(12)

                // Handle the result and map it to ListenableWorker.Result
                when (result) {

                    is com.tomiappdevelopment.imagepostscatalog.domain.util.Result.Error ->{ Result.success()}

                    is com.tomiappdevelopment.imagepostscatalog.domain.util.Result.Success -> { Result.retry()}
                }
            } catch (e: Exception) {
                // In case of exception, retry the work
                ListenableWorker.Result.retry()
            }
        }
    }
}