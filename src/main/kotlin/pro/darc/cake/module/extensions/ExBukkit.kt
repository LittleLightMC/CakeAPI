package pro.darc.cake.module.extensions

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import pro.darc.cake.CakeAPI
import java.util.logging.Level

val server: Server get() = Bukkit.getServer()

object Log: Plugin by CakeAPI.instance {
    // currying
    private val logger = { level: Level ->
        { msg: String -> getLogger().log(level, msg) }
    }
    val info = logger(Level.INFO)
    val warning = logger(Level.WARNING)
    val serve = logger(Level.SEVERE)
    val debug = logger(Level.FINE)
    val fine = logger(Level.FINE)
}

fun broadcast(message: String) {
    Bukkit.broadcastMessage(message)
}

inline fun broadcast(
    players: Iterable<Player> = Bukkit.getOnlinePlayers(),
    message: Player.() -> String
) {
    for (player in players)
        player.msg(message.invoke(player))
}

fun Collection<Player>.broadcast(
    message: Player.() -> String
) = broadcast(this, message)

fun Array<Player>.broadcast(
    message: Player.() -> String
) = broadcast(this.toList(), message)

// BaseComponent

fun broadcast(message: BaseComponent) {
    broadcastComponent { arrayOf(message) }
    Bukkit.getConsoleSender().msg(TextComponent.toLegacyText(message))
}

fun broadcast(message: Array<BaseComponent>) {
    broadcastComponent { message }
    Bukkit.getConsoleSender().msg(TextComponent.toLegacyText(*message))
}

inline fun broadcastComponent(
    players: Iterable<Player> = Bukkit.getOnlinePlayers(),
    message: Player.() -> Array<BaseComponent>
) {
    for (player in players) {
        player.msg(message.invoke(player))
    }
}
