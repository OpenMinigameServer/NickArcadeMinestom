package io.github.nickacpt.nickarcade.utils.interop

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

object AsyncCoroutineDispatcher : CoroutineDispatcher() {
    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Thread {
            block.run()
        }.start()
    }
}

object MinestomCoroutineDispatcher : CoroutineDispatcher() {
    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        AsyncCoroutineDispatcher.dispatch(context, block)
    }
}
