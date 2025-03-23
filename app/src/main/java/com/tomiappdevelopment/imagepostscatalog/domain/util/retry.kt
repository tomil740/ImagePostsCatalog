package com.tomiappdevelopment.imagepostscatalog.domain.util

import kotlinx.coroutines.delay

suspend fun <T> retry(
    times: Int,
    delayMillis: Long,
    block: suspend () -> T
): T {
    var attempt = 0
    var cause: Throwable? = null

    while (attempt < times) {
        try {
            return block()
        } catch (e: Throwable) {
            cause = e
            attempt++
            if (attempt < times) delay(delayMillis)
        }
    }
    throw cause ?: RuntimeException("Unknown error occurred during retry")
}
