package pro.darc.cake

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.TestOnly
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifecycleLoader
import pro.darc.cake.module.command.command
import pro.darc.cake.module.extensions.msg
import pro.darc.cake.module.extensions.subFile
import pro.darc.cake.module.flow.eventFlow
import pro.darc.cake.module.locale.asLocale
import pro.darc.cake.module.locale.sendDefaultLocale


open class CakeAPI : JavaPlugin() {

    companion object {
        lateinit var instance: CakeAPI
            private set

        @TestOnly
        internal fun setInstance(instance: CakeAPI) {
            CakeAPI.instance = instance
        }
    }

    override fun onLoad() {
        instance = this
        LifecycleLoader.runLifecycle(LifeCycle.CakeLoad)
        command("qwq") {
            executorPlayer {
                sender.msg("enter locale key:")

                val input = plugin.eventFlow<AsyncPlayerChatEvent>(
                    assign = sender,
                    priority = EventPriority.LOWEST,
                    ignoreCancelled = true,
                ).filter { it.player.name == sender.name }.map { it.message }.first()

                sender.sendDefaultLocale(input)
            }
        }
    }

    override fun onEnable() = LifecycleLoader.runLifecycle(LifeCycle.CakeEnable)

    override fun onDisable() = LifecycleLoader.runLifecycle(LifeCycle.CakeDisable)
}