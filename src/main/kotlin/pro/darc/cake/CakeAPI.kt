package pro.darc.cake

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.TestOnly
import pro.darc.cake.core.controller.BungeeCordController
import pro.darc.cake.core.controller.CommandController
import pro.darc.cake.core.controller.Controller
import pro.darc.cake.core.controller.ProviderController
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.core.inject.LifecycleLoader
import pro.darc.cake.module.command.command
import pro.darc.cake.module.extensions.registerEvents

internal fun provideCakeAPI(): CakeAPI {
    return Bukkit.getServer().pluginManager.getPlugin("CakeAPI") as CakeAPI?
        ?: throw IllegalAccessException("The plugin CakeAPI is not loaded yet")
}

open class CakeAPI : JavaPlugin() {

    companion object {
        lateinit var instance: CakeAPI
            private set

        @TestOnly
        internal fun setInstance(instance: CakeAPI) {
            CakeAPI.instance = instance
        }

        @LifeInject([LifeCycle.CakeEnable])
        @JvmStatic
        internal fun providerHook() {
            instance.controllers.forEach {
                it.onEnable()

                if (it is Listener) it.registerEvents(instance)
            }
        }
    }

    internal val bungeeCordController = BungeeCordController()
    internal val commandController = CommandController()
    internal val providerController = ProviderController()

    private val controllers = listOf(
        commandController, bungeeCordController, providerController,
    )

    override fun onLoad() {
        instance = this
        LifecycleLoader.runLifecycle(LifeCycle.CakeLoad)
        command("qwq") {
            executor {
                sender.sendMessage("wdnmd")
            }
        }
    }

    override fun onEnable() = LifecycleLoader.runLifecycle(LifeCycle.CakeEnable)

    override fun onDisable() = LifecycleLoader.runLifecycle(LifeCycle.CakeDisable)
}