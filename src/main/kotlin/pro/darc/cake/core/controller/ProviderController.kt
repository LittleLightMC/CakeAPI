package pro.darc.cake.core.controller

import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import pro.darc.cake.module.extensions.KListener
import pro.darc.cake.module.extensions.event
import pro.darc.cake.provideCakeAPI
import pro.darc.cake.utils.KClassComparator
import java.util.*
import kotlin.reflect.KClass

internal fun provideProviderController() = provideCakeAPI().providerController

internal class ProviderController: KListener, Controller {

    private val providerTree = TreeMap<String, TreeMap<KClass<*>, Any>>()

    fun register(plugin: Plugin, any: Any): Boolean {
        return providerTree.getOrPut(plugin.name) { TreeMap(KClassComparator) }
            .putIfAbsent(any::class, any) == null
    }

    fun unregister(plugin: Plugin, any: Any): Boolean {
        return providerTree.get(plugin.name)?.remove(any::class) == true
    }

    fun <T : Any> find(plugin: Plugin, kclass: KClass<T>): T {
        return providerTree[plugin.name]?.get(kclass) as T
    }

    override fun onEnable() {
        event<PluginDisableEvent> {
            providerTree.remove(plugin.name)
        }
    }
}
