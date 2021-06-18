package pro.darc.cake.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

enum class Priority(val number: UInt) {
    LOW(0u),
    MID(1u),
    HIGH(2u),
    MONITOR(3u);
}

class PriorityRunnable(private val runnable: Runnable, private val priority: Priority)
    : Runnable, Comparable<PriorityRunnable> {
    override fun compareTo(other: PriorityRunnable): Int {
        return (this.priority.number - other.priority.number).toInt()
    }

    override fun run() =  runnable.run()
}

fun Runnable.toPriority(priority: Priority = Priority.LOW): PriorityRunnable = PriorityRunnable(this, priority)

open class RunnablePool: Runnable {

    private val queue: PriorityBlockingQueue<PriorityRunnable> = PriorityBlockingQueue()

    fun push(value: Runnable, priority: Priority = Priority.LOW) = queue.put(value.toPriority(priority))

    override fun run() {
        // TODO create an event for it
        for (i in queue) {
            i.run()
        }
    }

}

interface TaggedRunnablePool<T: Comparable<T>> {
    val tagMap: MutableMap<T, RunnablePool>

    fun getOrCreatePool(tag: T): RunnablePool {
        if (!tagMap.containsKey(tag)) tagMap[tag] = RunnablePool()
        return tagMap[tag]!!
    }

    fun push(tag: T, priority: Priority = Priority.LOW, task: Runnable) {
        val pool = getOrCreatePool(tag)
        pool.push(task, priority)
    }

    fun runTag(tag: T) {
        tagMap[tag]?.run()
    }
}

class TaggedConcurrentRunnablePool
<T>
    : TaggedRunnablePool<T>
        where T: Comparable<T> {
    override val tagMap = ConcurrentHashMap<T, RunnablePool>()
}
