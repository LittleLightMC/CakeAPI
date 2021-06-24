package pro.darc.cake.module.command.arguments

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.*
import org.bukkit.entity.Player
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.TabCompleter
import pro.darc.cake.module.command.fail
import pro.darc.cake.module.extensions.color
import pro.darc.cake.module.extensions.onlinePlayers
import pro.darc.cake.module.extensions.textOf
import pro.darc.cake.module.locale.LocaleManager
import java.util.*

// PLAYER

val PLAYER_MISSING_PARAMETER = textOf(LocaleManager.asStringDefault("error missing player arg")!!)
val PLAYER_NOT_ONLINE =  textOf(LocaleManager.asStringDefault("error player not online")!!)

/**
 * returns a [Player] or null if the player is not online.
 */
fun Executor<*>.playerOrNull(
    index: Int,
    argMissing: BaseComponent = PLAYER_MISSING_PARAMETER
): Player? = string(index, argMissing).let { Bukkit.getPlayerExact(it) }

fun Executor<*>.player(
    index: Int,
    argMissing: BaseComponent = PLAYER_MISSING_PARAMETER,
    notOnline: BaseComponent = PLAYER_NOT_ONLINE
): Player = playerOrNull(index, argMissing) ?: fail(notOnline)

fun TabCompleter.player(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    onlinePlayers.mapNotNull {
        if(it.name.startsWith(arg, true)) it.name else null
    }
}

// OFFLINE PLAYER

fun Executor<*>.offlinePlayer(
    index: Int,
    argMissing: BaseComponent = PLAYER_MISSING_PARAMETER
): OfflinePlayer = string(index, argMissing).let {
    runCatching { UUID.fromString(it) }.getOrNull()?.let { Bukkit.getOfflinePlayer(it) }
        ?: Bukkit.getOfflinePlayer(it)
}

// GAMEMODE

val GAMEMODE_MISSING_PARAMETER = textOf(LocaleManager.asStringDefault("error missing gamemode arg")!!)
val GAMEMODE_NOT_FOUND = textOf(LocaleManager.asStringDefault("error notfound gamemode arg")!!)

/**
 * returns a [GameMode] or null if was not found.
 */
fun Executor<*>.gameModeOrNull(
    index: Int,
    argMissing: BaseComponent = GAMEMODE_MISSING_PARAMETER
): GameMode? = string(index, argMissing).run {
    toIntOrNull()?.let { GameMode.getByValue(it) } ?: runCatching { GameMode.valueOf(this.uppercase(Locale.getDefault())) }.getOrNull()
}

fun Executor<*>.gameMode(
    index: Int,
    argMissing: BaseComponent = GAMEMODE_MISSING_PARAMETER,
    notFound: BaseComponent = GAMEMODE_NOT_FOUND
): GameMode = gameModeOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.gameMode(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    GameMode.values().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name.lowercase(Locale.getDefault()) else null
    }
}

