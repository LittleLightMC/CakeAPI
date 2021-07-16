package pro.darc.cake.module.extensions

import me.clip.placeholderapi.PlaceholderAPI
import org.apache.commons.lang.text.StrSubstitutor
import org.bukkit.entity.Player

fun String.replaceByPAPI(player: Player?): String = PlaceholderAPI.setPlaceholders(player, this)

fun String.replaceByNamedArgument(args: Map<String, String>): String = StrSubstitutor(args, "%(", ")").replace(this)

