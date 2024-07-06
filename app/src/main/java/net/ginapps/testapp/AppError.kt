package net.ginapps.testapp

sealed class AppError {
    data object NoInternet : AppError()
    data class LogOut(val e: Throwable) : AppError()
    data class Unexpected(val e: Throwable?) : AppError()
}
