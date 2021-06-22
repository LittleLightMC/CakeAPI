package pro.darc.cake.module.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.Plugin
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.extensions.SimpleKListener

/**
 * Creates a event flow for any `PlayerEvent` that auto filters for the player that send the command.
 *
 * Use case:
 * ```kotlin
 * executorPlayer {
 *    sender.msg("Plz, send your faction description in the chat")
 *
 *    val description = commandPlayerEventFlow<AsyncPlayerChatEvent>()
 *                  .first()
 *                  .message
 *
 *    faction.description = description
 *
 *   sender.msg("You set the faction description to: $description")
 * }
 * ```
 */
inline fun <reified T : PlayerEvent> Executor<Player>.commandPlayerEventFlow(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    channel: Channel<T> = Channel(Channel.CONFLATED),
    listener: Listener = SimpleKListener(),
) = playerEventFlow(sender, command.plugin, priority, ignoreCancelled, channel, listener)

/**
 * Creates a event flow for [PlayerEvent] that auto filter for only events from [player].
 */

inline fun <reified T : PlayerEvent> Plugin.playerEventFlow(
    player: Player,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    channel: Channel<T> = Channel(Channel.CONFLATED),
    listener: Listener = SimpleKListener(),
) = playerEventFlow(player, this, priority, ignoreCancelled, channel, listener)

inline fun <reified T : PlayerEvent> playerEventFlow(
    player: Player,
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    channel: Channel<T> = Channel(Channel.CONFLATED),
    listener: Listener = SimpleKListener(),
) = eventFlow(T::class, plugin, player, priority, ignoreCancelled, channel, listener)
    .filter { it.player.name == player.name }

