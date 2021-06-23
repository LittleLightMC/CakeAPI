package pro.darc.cake.module.extensions

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

val protocol: ProtocolManager get() = ProtocolLibrary.getProtocolManager()

inline infix fun Player.sendPacket(pkg: Player.() -> PacketContainer) = protocol.sendServerPacket(this, pkg(this), false)

