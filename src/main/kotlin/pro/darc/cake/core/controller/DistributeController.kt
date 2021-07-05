package pro.darc.cake.core.controller

import org.bukkit.event.player.PlayerJoinEvent
import pro.darc.cake.core.controller.Controllers.distributeController
import pro.darc.cake.module.distribute.SendPlayerToServerRequest
import pro.darc.cake.module.distribute.WithDistributed
import pro.darc.cake.module.distribute.subscribeTopic
import pro.darc.cake.module.extensions.*

internal fun provideDistributeController() = distributeController

val serverName: String get() = Config.main.getString("server name")!!
lateinit var serverNameSafe: String private set

internal class DistributeController: KListener, Controller, WithDistributed {

    override fun onEnable() {
        subscribeTopic("cake-player-sendTo") { _, msg: SendPlayerToServerRequest ->
            val player = onlinePlayer(msg.uuid)
            player?.bungeecord?.sendToServer(msg.target)
        }

        event<PlayerJoinEvent> {
            player.bungeecord.getServer { serverName ->
                serverNameSafe = serverName
            }
        }
    }
}