package pro.darc.cake.module.menu.dsl

import org.bukkit.entity.Player
import java.util.*

fun MenuDSL.putPlayerData(player: Player, key: String, value: Any)
        = playerData.getOrPut(player) { WeakHashMap() }.put(key, value)

fun MenuDSL.getPlayerData(player: Player, key: String): Any?
        = playerData[player]?.get(key)
