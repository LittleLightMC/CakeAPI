package pro.darc.cake.module.command.arguments

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import pro.darc.cake.module.command.CommandFailException
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.TabCompleter
import pro.darc.cake.module.command.fail
import pro.darc.cake.module.extensions.*
import pro.darc.cake.module.locale.LocaleManager

// STRING

val MISSING_STRING_PARAMETER = textOf(LocaleManager.asStringDefault("error missing word arg"))

fun Executor<*>.string(
    index: Int,
    argMissing: BaseComponent = MISSING_STRING_PARAMETER
): String = args.getOrNull(index) ?: throw CommandFailException(argMissing, true)

val TEXT_STRING_PARAMETER = textOf(LocaleManager.asStringDefault("error missing text arg"))

fun Executor<*>.text(
    startIndex: Int,
    endIndex: Int = args.size,
    separator: String = " ",
    argMissing: BaseComponent = TEXT_STRING_PARAMETER
): String {
    if(startIndex >= args.size) fail(argMissing)
    return array(startIndex, endIndex) { string(it) }.joinToString(separator)
}

// BOOLEAN

val MISSING_BOOLEAN_PARAMETER = textOf(LocaleManager.asStringDefault("error missing boolean arg"))
val BOOLEAN_FORMAT = textOf(LocaleManager.asStringDefault("error boolean arg"))

/**
 * Returns [Boolean] or null if was not able to parse to Boolean.
 */
fun Executor<*>.booleanOrNull(
    index: Int,
    argMissing: BaseComponent = MISSING_BOOLEAN_PARAMETER,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean? = string(index, argMissing).toBooleanOrNull(trueCases, falseCases)

fun Executor<*>.boolean(
    index: Int,
    argMissing: BaseComponent = MISSING_BOOLEAN_PARAMETER,
    booleanFormat: BaseComponent = BOOLEAN_FORMAT,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean = booleanOrNull(index, argMissing, trueCases, falseCases) ?: fail(booleanFormat)

fun TabCompleter.boolean(
    index: Int,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): List<String> = argumentCompleteBuilder(index) { arg ->
    listOf(*trueCases, *falseCases).filter { it.startsWith(arg, true) }
}

val MISSING_NUMBER_PARAMETER = textOf(LocaleManager.asStringDefault("error missing number arg"))
val NUMBER_FORMAT = textOf(LocaleManager.asStringDefault("error number arg"))

// INT

/**
 * Returns [Int] or null if was not able to parse to Int.
 */
fun Executor<*>.intOrNull(
    index: Int,
    argMissing: BaseComponent = MISSING_NUMBER_PARAMETER
): Int? = string(index, argMissing).toIntOrNull()

fun Executor<*>.int(
    index: Int,
    argMissing: BaseComponent = MISSING_NUMBER_PARAMETER,
    numberFormat: BaseComponent = NUMBER_FORMAT
): Int = intOrNull(index, argMissing) ?: fail(numberFormat)

// DOUBLE

/**
 * Returns [Double] or null if was not able to parse to Double.
 */
fun Executor<*>.doubleOrNull(
    index: Int,
    argMissing: BaseComponent = MISSING_NUMBER_PARAMETER
): Double? = string(index, argMissing).toDoubleOrNull()?.takeIf { it.isFinite() }

fun Executor<*>.double(
    index: Int,
    argMissing: BaseComponent = MISSING_NUMBER_PARAMETER,
    numberFormat: BaseComponent = NUMBER_FORMAT
): Double = doubleOrNull(index, argMissing) ?: fail(numberFormat)

