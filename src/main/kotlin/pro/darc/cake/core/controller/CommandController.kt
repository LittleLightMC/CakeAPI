package pro.darc.cake.core.controller

import org.bukkit.command.Command
import org.bukkit.event.server.PluginDisableEvent
import pro.darc.cake.module.extensions.KListener
import pro.darc.cake.module.extensions.event
import pro.darc.cake.provideCakeAPI
import pro.darc.cake.module.extensions.unregister

internal fun provideCommandController() = provideCakeAPI().commandController

internal class CommandController: KListener, Controller {

    val commands = hashMapOf<String, MutableList<Command>>()

    override fun onEnable() {
        event<PluginDisableEvent> {
            commands.remove(plugin.name)?.forEach {
                it.unregister()
            }
        }
    }
}
