package pro.darc.cake.utils

import org.bstats.bukkit.Metrics
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.cake


object BStats {

    const val pluginId = 11782
    lateinit var metrics: Metrics
        private set

    @LifeInject([LifeCycle.CakeLoad, LifeCycle.CakeReload])
    @JvmStatic
    fun init() {
        metrics = Metrics(cake, pluginId)
    }

}
