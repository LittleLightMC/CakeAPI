package pro.darc.cake.module.extensions

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

private val unicodeRegex = "((\\\\u)([0-9]{4}))".toRegex()

fun String.javaUnicodeToCharacter(): String = unicodeRegex.replace(this) {
    String(charArrayOf(it.destructured.component3().toInt(16).toChar()))
}

fun <T> T.print(): T = also { println(it) }

fun String.centralize(
    length: Int,
    spacer: String = " ",
    prefix: String = "",
    suffix: String = ""
): String {
    if (this.length >= length) return this
    val part = prefix + spacer.repeat((length - this.length) / 2) + suffix
    return part + this + part
}

val TRUE_CASES = arrayOf("true", "正确", "是", "yes")
    get() = field.clone()
val FALSE_CASES = arrayOf("false", "错误", "否", "no")
    get() = field.clone()

fun String.toBooleanOrNull(
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean? = when {
    trueCases.any { it.equals(this, true) } -> true
    falseCases.any { it.equals(this, true) } -> false
    else -> null
}

/**
 * return a colorized string
 *
 * Support HEX color introducing from 1.16
 *
 * @param hexPattern provide a hex color tag format
 */
fun String.colorize(hexPattern: Pattern = Pattern.compile("(?<!\\\\\\\\)(#[a-fA-F0-9]{6})")): String {
    var res = this.replace('&', '§')
    val matcher = hexPattern.matcher(this)
    while (matcher.find()) {
        val color = this.substring(matcher.start(), matcher.end())
        res = this.replace(color, "${ChatColor.of(color)}")
    }
    return res
}
