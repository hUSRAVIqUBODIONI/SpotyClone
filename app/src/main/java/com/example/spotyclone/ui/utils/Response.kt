package com.example.spotyclone.ui.utils

sealed class Response<T>(val value: T? = null, val message: String? = null) {
    class Success<T>(value: T): Response<T>(value = value)
    class Error<T>(message: String): Response<T>(message=message)
}