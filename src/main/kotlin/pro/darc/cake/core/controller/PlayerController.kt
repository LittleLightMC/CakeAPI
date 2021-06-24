package pro.darc.cake.core.controller

import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.PluginDisableEvent
import pro.darc.cake.module.extensions.*
import pro.darc.cake.utils.ChatInput
import pro.darc.cake.utils.PlayerCallback
import pro.darc.cake.utils.collections.onlinePlayerMapOf

internal fun providePlayerController() = Controllers.playerController

internal class PlayerController(
) : KListener, Controller {

    internal val inputCallbacks by lazy { cake.onlinePlayerMapOf<ChatInput>() }
    internal val functionsMove by lazy { cake.onlinePlayerMapOf<PlayerCallback<Boolean>>() }
    internal val functionsQuit by lazy { cake.onlinePlayerMapOf<PlayerCallback<Unit>>() }

    override fun onEnable() {
        event<AsyncPlayerChatEvent>(ignoreCancelled = true) {
            if (message.isNotBlank()) {
                val input = inputCallbacks.remove(player)
                if (input != null) {
                    if (input.sync) scheduler { input.callback(player, message) }.runTask(cake)
                    else input.callback(player, message)
                    isCancelled = true
                }
            }
        }
        event<PlayerMoveEvent>(ignoreCancelled = true) {
            if (displaced) {
                if (functionsMove[player]?.run { callback.invoke(player) } == true) {
                    isCancelled = true
                }
            }
        }
        event<PluginDisableEvent> {
            inputCallbacks.entries.filter { it.value.plugin == plugin }.forEach {
                inputCallbacks.remove(it.key)
            }
            functionsMove.entries.filter { it.value.plugin == plugin }.forEach {
                functionsMove.remove(it.key)
            }
            functionsQuit.entries.filter { it.value.plugin == plugin }.forEach {
                functionsQuit.remove(it.key)
            }
        }
    }
}
