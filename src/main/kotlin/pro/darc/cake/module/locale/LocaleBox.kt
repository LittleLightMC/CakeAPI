package pro.darc.cake.module.locale

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.configuration.serialization.SerializableAs
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.*

interface LocaleBox {
    fun send(receiver: CommandSender)
}

class SimpleStringBox(val string: String): LocaleBox {
    override fun send(receiver: CommandSender) {
        receiver.sendMessage(string.colorize())
    }

}

abstract class ComplexLocaleBox: LocaleBox, ConfigurationSerializable

object Box {
    private val boxList: List<ComplexLocaleBox> = listOf()

    @LifeInject([LifeCycle.CakeLoad])
    @JvmStatic
    fun init() {
        boxList.forEach {
            ConfigurationSerialization.registerClass(it::class.java)
        }
    }

}

@SerializableAs("Text")
class TextLocaleBox(
    val text: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        receiver.sendMessage(text.colorize())
    }
    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "text" to text,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): TextLocaleBox {
            return TextLocaleBox(args["text"] as String)
        }
    }

}

class PlayerCommandLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) = receiver.isPlayerThen {
        this.performCommand(command.replaceByPAPI(this))
    }

    override fun serialize(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

}
