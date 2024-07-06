package net.ginapps.testapp.core

import net.ginapps.testapp.AppError

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}

fun <T> Result<T>.onSuccess(call: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        call(this.data)
    }

    return this
}

fun <T, R> Result<T>.mapSuccess(call: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(call(this.data))
    is Result.Error -> this
}

fun <T> Result<T>.onError(call: (AppError) -> Unit): Result<T> {
    if (this is Result.Error) {
        call(this.error)
    }

    return this
}
