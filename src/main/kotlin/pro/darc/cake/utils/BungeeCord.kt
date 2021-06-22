package pro.darc.cake.utils

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import pro.darc.cake.core.controller.provideBungeeCordController
import pro.darc.cake.module.extensions.sendBungeeCord

typealias ResponseCallback = (message: ByteArray) -> Unit

class BungeeCordRequest(
    val player: Player,
    val subChannel: String,
    val request: ByteArray? = null,
    val responseCallback: ResponseCallback? = null
) {
    fun send() {
        val out = ByteStreams.newDataOutput()
        out.writeUTF(subChannel)
        if (request != null) out.write(request)

        player.sendBungeeCord(out.toByteArray())

        if (responseCallback != null) provideBungeeCordController().addToQueue(this)
    }
}
