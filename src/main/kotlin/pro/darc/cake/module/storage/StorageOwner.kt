package pro.darc.cake.module.storage

import org.bukkit.entity.Player
import pro.darc.cake.module.extensions.server
import java.util.*

val OWNER_REGEX = Regex("^(server|group|user):*(\\w+)?$")

/**
 * Indicating the owner
 *
 * @param note an note of owner
 *
 * e.g.
 * ```
 * "user:1ac884e6-1353-4af0-a0da-434aae6e3388"
 * "group:default"
 * "server"
 * ```
 */
class StorageOwner(private val note: String = "server") {

    var type: StorageOwnerType
        private set
    var id: String
        private set

    init {
        val (typeStr, idStr) = OWNER_REGEX.find(note, 0)!!.destructured
        type = StorageOwnerType.fromString(typeStr)
        id = idStr
    }

    fun getPlayer(): Player? =
        when (type) {
            StorageOwnerType.USER -> server.getPlayer(UUID.fromString(id))
            else -> null
        }

}

enum class StorageOwnerType {
    USER,
    GROUP,
    SERVER,
    UNKNOWN;

    companion object {
        fun fromString(typeStr: String): StorageOwnerType = when(typeStr) {
            "server" -> SERVER
            "user" -> USER
            "group" -> GROUP
            else -> UNKNOWN
        }
    }
}
