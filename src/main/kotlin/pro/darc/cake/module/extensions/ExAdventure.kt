package pro.darc.cake.module.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender

fun Component.toBaseComponent(): Array<BaseComponent> {
    return BungeeComponentSerializer.get().serialize(this)
}

fun Component.sendTo(receiver: CommandSender) {
    receiver.msg(toBaseComponent())
}

fun CommandSender.msg(comp: Component) {
    comp.sendTo(this)
}
