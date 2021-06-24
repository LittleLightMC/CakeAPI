package pro.darc.cake

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.TestOnly
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifecycleLoader
import pro.darc.cake.module.command.command
import pro.darc.cake.module.extensions.msg
import pro.darc.cake.module.flow.eventFlow
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
    }

    override fun onEnable() = LifecycleLoader.runLifecycle(LifeCycle.CakeEnable)

    override fun onDisable() = LifecycleLoader.runLifecycle(LifeCycle.CakeDisable)
}