package pro.darc.cake.module.command.arguments

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.fail
import pro.darc.cake.module.extensions.color
import pro.darc.cake.module.extensions.textOf
import pro.darc.cake.module.locale.LocaleManager

// intRange
val MISSING_RANGE_PARAMETER = textOf(LocaleManager.asStringDefault("error missing range arg"))
val INT_RANGE_FORMAT = textOf(LocaleManager.asStringDefault("error range must int"))

/**
 * Returns [IntRange] or null if was not able to parse to IntRange given the [separator].
 */
fun Executor<*>.intRangeOrNull(
    index: Int,
    argMissing: BaseComponent = MISSING_RANGE_PARAMETER,
    separator: String = ".."
): IntRange? {
    val slices = string(index, argMissing).split(separator)
    val min = slices.getOrNull(0)?.toIntOrNull()
    val max = slices.getOrNull(1)?.toIntOrNull()

    return max?.let { min?.rangeTo(it) }
}

fun Executor<*>.intRange(
    index: Int,
    argMissing: BaseComponent = MISSING_RANGE_PARAMETER,
    rangeFormat: BaseComponent = INT_RANGE_FORMAT,
    separator: String = ".."
): IntRange = intRangeOrNull(index, argMissing, separator)
    ?: fail(rangeFormat)
