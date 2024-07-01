package net.ginapps.testapp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MainCoroutineContext(context: CoroutineContext) : CoroutineContext by context
class IoCoroutineContext(context: CoroutineContext) : CoroutineContext by context

fun CoroutineContext.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit,
) = CoroutineScope(this).launch(context) { block() }

fun <T> CoroutineContext.async(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = CoroutineScope(this).async(context) { block() }

suspend fun <T> CoroutineContext.execute(
    block: suspend CoroutineScope.() -> T,
) = withContext(this, block)
