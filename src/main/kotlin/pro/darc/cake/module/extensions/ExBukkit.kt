package pro.darc.cake.module.extensions

import org.bukkit.Bukkit
import org.bukkit.Server
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
