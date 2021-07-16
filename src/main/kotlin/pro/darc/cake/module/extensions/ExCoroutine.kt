package pro.darc.cake.module.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import pro.darc.cake.utils.collections.OnlinePlayerMap
import pro.darc.cake.utils.collections.onlinePlayerMapOf

class PlayerCoroutineScope(
    private val job: Job,
    val coroutineScope: CoroutineScope,
) {
    fun cancelJobs() = job.cancel()
}


fun Plugin.getPluginCoroutineScope(job: Job = SupervisorJob()): CoroutineScope = CoroutineScope(BukkitDispatchers.SYNC + job)

private val Plugin.playersCoroutineScope: OnlinePlayerMap<PlayerCoroutineScope> by lazy {
    onlinePlayerMapOf()
}

private fun Plugin.newPlayerCoroutineScope(): PlayerCoroutineScope {
    val job = SupervisorJob()
    return PlayerCoroutineScope(
        job,
        CoroutineScope(BukkitDispatchers.SYNC + job)
    )
}

fun Plugin.getPlayerCoroutineScope(player: Player): CoroutineScope {
    return playersCoroutineScope[player]?.coroutineScope
        ?: newPlayerCoroutineScope().also {
            playersCoroutineScope.put(player, it) { playerCoroutineScope ->
                playerCoroutineScope.cancelJobs()
            }
        }.coroutineScope
}
