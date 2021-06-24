package pro.darc.cake.module.command

import net.md_5.bungee.api.chat.BaseComponent
import pro.darc.cake.module.extensions.asText
import pro.darc.cake.module.locale.sendDefaultLocale
import pro.darc.cake.utils.collections.ExpirationList
import pro.darc.cake.utils.collections.ExpirationMap

typealias ErrorHandler = Executor<*>.(Throwable) -> Unit

val defaultErrorHandler: ErrorHandler = {
    sender.sendDefaultLocale("text default command execution fail")
    it.printStackTrace()
}

class CommandFailException(
    val senderMessage: BaseComponent? = null,
    val argMissing: Boolean = false,
    inline val execute: suspend () -> Unit = {}
): RuntimeException()

fun Executor<*>.fail(
    senderMessage: BaseComponent? = null,
    execute: suspend () -> Unit = {}
): Nothing = throw CommandFailException(senderMessage, execute = execute)

fun Executor<*>.fail(
    senderMessage: String = "",
    execute: suspend () -> Unit = {}
): Nothing = fail(senderMessage.takeIf { it.isNotEmpty() }?.asText(), execute = execute)

fun Executor<*>.fail(
    senderMessage: List<String> = listOf(),
    execute: suspend () -> Unit = {}
): Nothing = fail(senderMessage.takeIf { it.isNotEmpty() }?.asText(), execute = execute)

inline fun<T> ExpirationList<T>.failIfContains(
    element: T,
    execute: (missingTime: Int) -> Unit
) {
    missingTime(element)?.let(execute)?.run {
        throw  CommandFailException()
    }
}

inline fun <K> ExpirationMap<K, *>.failIfContains(
    key: K,
    execute: (missingTime: Long) -> Unit
) {
    missingTime(key)?.let(execute)?.run {
        throw CommandFailException()
    }
}
