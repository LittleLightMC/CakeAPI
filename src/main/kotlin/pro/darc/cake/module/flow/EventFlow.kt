package pro.darc.cake.module.flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import pro.darc.cake.module.extensions.SimpleKListener
import pro.darc.cake.module.extensions.event
import pro.darc.cake.module.extensions.events
import pro.darc.cake.module.extensions.unregisterListener
import kotlin.reflect.KClass

inline fun <reified T : Event> Plugin.eventFlow(
    assign: Player? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    channel: Channel<T> = Channel<T>(Channel.CONFLATED),
    listener: Listener = events {},
    assignListener: Listener = events {}
): Flow<T> = eventFlow(T::class, this, assign, priority, ignoreCancelled, channel, listener, assignListener)

/**
 * Create a Event Flow that receives the specified Event [type].
 *
 * [assign] is use for auto cancel the Flow when the Player disconnects.
 */
@OptIn(DelicateCoroutinesApi::class)
fun <T : Event> eventFlow(
    type: KClass<T>,
    plugin: Plugin,
    assign: Player? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    channel: Channel<T> = Channel<T>(Channel.CONFLATED),
    listener: Listener = SimpleKListener(),
    assignListener: Listener = SimpleKListener()
): Flow<T> {

    val flow = channel.consumeAsFlow().onStart {
        listener.event(plugin, type, priority, ignoreCancelled) {
            GlobalScope.launch {
                if (!channel.isClosedForSend && !channel.isClosedForReceive)
                    channel.send(this@event)
            }
        }
    }

    val assignListener: Listener? = if (assign != null)
        assignListener.apply {
            fun PlayerEvent.closeChannel() {
                if (!channel.isClosedForSend && player.name == assign.name)
                    channel.close()
            }

            event<PlayerQuitEvent> { closeChannel() }
            event<PlayerKickEvent> { closeChannel() }
        }
    else null

    channel.invokeOnClose {
        listener.unregisterListener()
        assignListener?.unregisterListener()
    }

    return flow
}
