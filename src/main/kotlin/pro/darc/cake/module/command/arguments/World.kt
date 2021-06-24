package pro.darc.cake.module.command.arguments

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.TabCompleter
import pro.darc.cake.module.command.fail
import pro.darc.cake.module.extensions.color
import pro.darc.cake.module.extensions.textOf
import pro.darc.cake.module.locale.LocaleManager

// WORLD

val MISSING_WORLD_ARGUMENT = textOf(LocaleManager.asStringDefault("error missing world arg"))
val WORLD_NOT_FOUND = textOf(LocaleManager.asStringDefault("error notfound world arg"))

/**
 * Returns [World] or null if was not found.
 */
fun Executor<*>.worldOrNull(
    index: Int,
    argMissing: BaseComponent = MISSING_WORLD_ARGUMENT
): World? = string(index, argMissing).let { Bukkit.getWorld(it) }

fun Executor<*>.world(
    index: Int,
    argMissing: BaseComponent = MISSING_WORLD_ARGUMENT,
    notFound: BaseComponent = WORLD_NOT_FOUND
): World = worldOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.world(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Bukkit.getWorlds().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name else null
    }
}

// COORDINATE

val MISSING_COORDINATE_ARGUMENT = textOf(LocaleManager.asStringDefault("error coordinate arg"))
val COORDINATE_NUMBER_FORMAT = textOf(LocaleManager.asStringDefault("error input coordinate arg"))

fun Executor<Player>.coordinate(
    xIndex: Int, yIndex: Int, zIndex: Int,
    argMissing: BaseComponent = MISSING_COORDINATE_ARGUMENT,
    numberFormat: BaseComponent = COORDINATE_NUMBER_FORMAT
): Location = coordinate(xIndex, yIndex, zIndex, sender.world, argMissing, numberFormat)

fun Executor<*>.coordinate(
    xIndex: Int, yIndex: Int, zIndex: Int, world: World,
    argMissing: BaseComponent = MISSING_COORDINATE_ARGUMENT,
    numberFormat: BaseComponent = COORDINATE_NUMBER_FORMAT
): Location {

    fun double(index: Int) = double(index, argMissing, numberFormat)

    return Location(world, double(xIndex), double(yIndex), double(zIndex))
}
