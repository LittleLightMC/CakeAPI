package pro.darc.cake.core.controller

import org.bukkit.event.Listener
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.cake
import pro.darc.cake.module.extensions.registerEvents

interface Controller {
    fun onEnable()
}

object Controllers {

    internal val playerController: PlayerController = PlayerController()
    internal val bungeeCordController = BungeeCordController()
    internal val commandController = CommandController()
    internal val providerController = ProviderController()
    internal val menuController = MenuController()

    private val controllers = listOf(
        commandController, bungeeCordController, providerController, menuController, playerController,
    )

    @LifeInject([LifeCycle.CakeEnable])
    @JvmStatic
    fun providerHook() {
        controllers.forEach {
            it.onEnable()

            if (it is Listener) it.registerEvents(cake)
        }
    }

}
