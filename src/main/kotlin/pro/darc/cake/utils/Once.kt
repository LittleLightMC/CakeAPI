package pro.darc.cake.utils

import java.util.concurrent.atomic.AtomicBoolean

class Once(private val task: Runnable) {
    private val done = AtomicBoolean()

    operator fun invoke() {
        run()
    }

    private fun run() {
        if (done.get()) return;
        if (done.compareAndSet(false, true)) {
            task.run()
        }
    }
}