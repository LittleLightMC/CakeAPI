package pro.darc.cake.module.distribute

import org.redisson.api.*

interface WithDistributed

fun WithDistributed.lock(key: String): RLock = redisson.getLock(key)
fun WithDistributed.rwlock(key: String): RReadWriteLock = redisson.getReadWriteLock(key)

fun<K, T> WithDistributed.getMap(key: String): RMap<K, T> = redisson.getMap(key)
fun<T> WithDistributed.getList(key: String): RList<T> = redisson.getList(key)
fun<T> WithDistributed.getQueue(key: String): RQueue<T> = redisson.getQueue(key)
fun<T> WithDistributed.getDeque(key: String): RDeque<T> = redisson.getDeque(key)

val WithDistributed.distributedKeys: RKeys get() = redisson.keys
fun WithDistributed.searchKey(pattern: String): Iterable<String> = distributedKeys.getKeysByPattern(pattern)
fun WithDistributed.deleteKey(vararg key: String): Long = distributedKeys.delete(*key)
fun WithDistributed.deleteKeyByPattern(pattern: String): Long = distributedKeys.deleteByPattern(pattern)

fun<T> WithDistributed.getBucket(key: String): RBucket<T> = redisson.getBucket(key)
fun WithDistributed.getBinaryStream(key: String): RBinaryStream = redisson.getBinaryStream(key)
fun<T> WithDistributed.getGeo(key: String): RGeo<T> = redisson.getGeo(key)
fun WithDistributed.getBitSet(key: String): RBitSet = redisson.getBitSet(key)
fun WithDistributed.getAtomicLong(key: String): RAtomicLong = redisson.getAtomicLong(key)
fun WithDistributed.getAtomicDouble(key: String): RAtomicDouble = redisson.getAtomicDouble(key)

fun WithDistributed.getTopic(key: String): RTopic = redisson.getTopic(key)
inline fun<reified T> WithDistributed.subscribeTopic(
    key: String,
    crossinline action: (channel: CharSequence, msg: T) -> Unit
): Int = getTopic(key).addListener(T::class.java) { channel, msg ->
        action(channel, msg)
    }
fun WithDistributed.unsubscribeTopic(
    key: String,
    id: Array<Int>,
) = getTopic(key).removeListener(*id)

fun WithDistributed.getPatternTopic(pattern: String): RPatternTopic = redisson.getPatternTopic(pattern)
inline fun<reified T> WithDistributed.subscribePatternTopic(
    key: String,
    crossinline action: (pattern: CharSequence, channel: CharSequence, msg: T) -> Unit
): Int = getPatternTopic(key).addListener(T::class.java) { pattern, channel, msg ->
    action(pattern, channel, msg)
}
fun WithDistributed.unsubscribePatternTopic(
    key: String,
    id: Int,
) = getPatternTopic(key).removeListener(id)

fun WithDistributed.getLongAdder(key: String): RLongAdder = redisson.getLongAdder(key)
fun WithDistributed.getDoubleAdder(key: String): RDoubleAdder = redisson.getDoubleAdder(key)
