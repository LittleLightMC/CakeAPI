package pro.darc.cake.module.extensions

import com.okkero.skedule.BukkitDispatcher
import com.okkero.skedule.BukkitSchedulerController
import com.okkero.skedule.SynchronizationContext
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pro.darc.cake.utils.getTakeValuesOrNull
import pro.darc.cake.utils.registerCoroutineContextTakes
import pro.darc.cake.utils.unregisterCoroutineContextTakes
import kotlin.time.Duration

val BukkitSchedulerController.contextSync get() = SynchronizationContext.SYNC
val BukkitSchedulerController.contextAsync get() = SynchronizationContext.ASYNC

suspend fun BukkitSchedulerController.switchToSync() = switchContext(contextSync)
suspend fun BukkitSchedulerController.switchToAsync() = switchContext(contextAsync)

val Plugin.BukkitDispatchers get() = PluginDispatcher(this as JavaPlugin)
val Plugin.BukkitDispatchersSafe get() = PluginDispatcher(JavaPlugin.getProvidingPlugin(this::class.java))

@JvmInline
value class PluginDispatcher(val plugin: JavaPlugin) {
    val ASYNC get() = BukkitDispatcher(plugin, true)
    val SYNC get() = BukkitDispatcher(plugin, false)
}


suspend fun BukkitSchedulerController.takeMaxPerTick(time: Duration) {
    val takeValues = getTakeValuesOrNull(context)

    when (takeValues) {
        null -> registerCoroutineContextTakes(context, time)
        else -> if (takeValues.wasTimeExceeded()) {
            unregisterCoroutineContextTakes(context)
            waitFor(1)
        }
    }
}
