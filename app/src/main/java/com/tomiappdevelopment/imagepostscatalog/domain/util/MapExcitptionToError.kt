package com.tomiappdevelopment.imagepostscatalog.domain.util

fun mapHttpErrorCodeToDataError(code: Int): DataError.Network {
    return when (code) {
        408 -> DataError.Network.REQUEST_TIMEOUT // Request Timeout
        401 -> DataError.Network.UNAUTHORIZED // Unauthorized
        409 -> DataError.Network.CONFLICT // Conflict
        429 -> DataError.Network.TOO_MANY_REQUESTS // Too Many Requests
        413 -> DataError.Network.PAYLOAD_TOO_LARGE // Payload Too Large
        in 500..599 -> DataError.Network.SERVER_ERROR // Server Errors
        else -> DataError.Network.UNKNOWN // Catch-all for unhandled cases
    }
}

fun mapThrowableToDataError(throwable: Throwable): DataError.Network {
    return when (throwable) {
        is java.net.UnknownHostException -> DataError.Network.NO_INTERNET // No Internet
        is java.net.SocketTimeoutException -> DataError.Network.REQUEST_TIMEOUT // Timeout
        else -> DataError.Network.UNKNOWN // Generic fallback for unhandled exceptions
    }
}