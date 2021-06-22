package pro.darc.cake.module.extensions

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

fun String.replaceByPAPI(player: Player?): String = PlaceholderAPI.setPlaceholders(player, this)

