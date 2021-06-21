package pro.darc.cake.module.command

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import pro.darc.cake.module.extensions.msg
import pro.darc.cake.module.extensions.suggestCommand

const val SEND_SUB_COMMANDS_LABEL_PLACEHOLDER = "{label}"
const val SEND_SUB_COMMANDS_NAME_PLACEHOLDER = "{subcmd}"
const val SEND_SUB_COMMANDS_DESCRIPTION_PLACEHOLDER = "{description}"

val SEND_SUB_COMMANDS_DEFAULT_FORMAT = "${ChatColor.BLUE}/$SEND_SUB_COMMANDS_LABEL_PLACEHOLDER ${ChatColor.YELLOW}$SEND_SUB_COMMANDS_NAME_PLACEHOLDER ${ChatColor.BLUE}-> ${ChatColor.GRAY}$SEND_SUB_COMMANDS_DESCRIPTION_PLACEHOLDER"

val Executor<Player>.player: Player get() = sender

fun Executor<*>.sendSubCommandsList(
    format: String = SEND_SUB_COMMANDS_DEFAULT_FORMAT,
    needCommandPermission: Boolean = true
) {
    val subcmds = command.subCommands.filterNot {
        needCommandPermission && !it.permission?.let { it1 -> sender.hasPermission(it1) }!!
    }.associateWith {
        format.replace(SEND_SUB_COMMANDS_LABEL_PLACEHOLDER, label, true)
            .replace(SEND_SUB_COMMANDS_NAME_PLACEHOLDER, it.name, true)
            .replace(SEND_SUB_COMMANDS_DESCRIPTION_PLACEHOLDER, it.description, true)
    }
    if(subcmds.isEmpty()) command.permissionMessage?.let { fail(it) }

    if(sender is Player) {
        subcmds.map { (key, value) ->
            value.suggestCommand("/$label ${key.name}")
        }.forEach { sender.msg(it) }
    } else {
        subcmds.values.forEach { sender.msg(it) }
    }
}