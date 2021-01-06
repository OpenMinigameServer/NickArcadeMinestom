package io.github.nickacpt.nickarcade.utils.interop

import kotlinx.coroutines.*
import net.minestom.server.MinecraftServer
import kotlin.coroutines.CoroutineContext

object CoroutineSession {

    private val scope: CoroutineScope by lazy {
        CoroutineScope(AsyncCoroutineDispatcher)
    }

    fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit): Job {

        if (dispatcher == Dispatchers.Unconfined) {
            // If the dispatcher is unconfined. Always schedule immediately.
            return launchInternal(dispatcher, CoroutineStart.UNDISPATCHED, f)
        }

        return launchInternal(dispatcher, CoroutineStart.DEFAULT, f)
    }

    /**
     * Executes the launch
     */
    private fun launchInternal(
        dispatcher: CoroutineContext,
        coroutineStart: CoroutineStart,
        f: suspend CoroutineScope.() -> Unit
    ): Job {
        // Launch a new coroutine on the current thread thread on the plugin scope.
        return scope.launch(dispatcher, coroutineStart) {
            try {
                // The user may or may not launch multiple sub suspension operations. If
                // one of those fails, only this scope should fail instead of the plugin scope.
                coroutineScope {
                    f.invoke(this)
                }
            } catch (e: CancellationException) {
                MinecraftServer.LOGGER.info("Coroutine has been cancelled.")
            } catch (e: Exception) {
                MinecraftServer.LOGGER.error(
                    "This is not an error of MCCoroutine! See sub exception for details.",
                    e
                )
            }
        }
    }
}