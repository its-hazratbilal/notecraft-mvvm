package com.hazratbilal.notecraft.remote

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Idle<T> : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
    class Success<T>(data: T) : NetworkResult<T>(data)
    class AuthError<T>(message: String) : NetworkResult<T>(null, message)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
}