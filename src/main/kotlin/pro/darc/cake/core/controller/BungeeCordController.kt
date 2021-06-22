package pro.darc.cake.core.controller

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import pro.darc.cake.CakeAPI
import pro.darc.cake.module.extensions.cake
import pro.darc.cake.provideCakeAPI
import pro.darc.cake.utils.BungeeCordRequest
import java.nio.ByteBuffer
import java.nio.charset.Charset

internal fun provideBungeeCordController() = provideCakeAPI().bungeeCordController

internal class BungeeCordController: PluginMessageListener, Controller {

    private val queue = mutableListOf<BungeeCordRequest>()

    override fun onEnable() {
        Bukkit.getServer().messenger.registerOutgoingPluginChannel(cake, "BungeeCord")
        Bukkit.getServer().messenger.registerIncomingPluginChannel(cake, "BungeeCord", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "BungeeCord") return

        val buffer = ByteBuffer.wrap(message)
        val subChannel = buffer.readUTF()
        val request = queue.firstOrNull { it.subChannel == subChannel }
        if(request?.responseCallback != null) {
            val infoBuffer = buffer.slice()
            val info = ByteArray(infoBuffer.remaining())
            infoBuffer.get(info)
            request.responseCallback.invoke(info)
            queue.remove(request)
        }
    }

    fun sendBungeeCord(player: Player, message: ByteArray)
            = player.sendPluginMessage(cake, "BungeeCord", message)

    fun addToQueue(request: BungeeCordRequest) = queue.add(request)

    private fun ByteBuffer.readUTF() = String(ByteArray(short.toInt()).apply { get(this) }, Charset.forName("UTF-8"))
}