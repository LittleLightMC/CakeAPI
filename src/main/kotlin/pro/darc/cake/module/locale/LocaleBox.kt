package pro.darc.cake.module.locale

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.Player
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.*
import kotlin.reflect.KClass

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
    private val boxList: List<KClass<out ComplexLocaleBox>> = listOf(
        TextLocaleBox::class, PlayerCommandLocaleBox::class,
        ServerCommandLocaleBox::class, SoundLocaleBox::class,
        TitleLocaleBox::class, BarLocaleBox::class, JSONLocaleBox::class,
        ActionLocaleBox::class,
    )

    @LifeInject([LifeCycle.CakeLoad])
    @JvmStatic
    fun init() {
        boxList.forEach {
            ConfigurationSerialization.registerClass(it.java)
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

@SerializableAs("PlayerCommand")
class PlayerCommandLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) = receiver.isPlayerThen {
        this.performCommand(command.replaceByPAPI(this))
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "command" to command,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): PlayerCommandLocaleBox {
            return PlayerCommandLocaleBox(args["command"] as String)
        }
    }
}


@SerializableAs("ServerCommand")
class ServerCommandLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        val parsed = command.colorize().let {
            if (receiver.isPlayer()) it.replaceByPAPI(receiver as Player)
            else it.replaceByPAPI(null)
        }
        server.dispatchCommand(console, parsed)
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "command" to command,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): PlayerCommandLocaleBox {
            return PlayerCommandLocaleBox(args["command"] as String)
        }
    }
}


@SerializableAs("Sound")
class SoundLocaleBox(
    val sound: String,
    val volume: Float = 3F,
    val pitch: Float = 0.5F,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            playSound(location, sound, volume, pitch)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "sound" to sound,
            "volume" to volume,
            "pitch" to pitch,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): SoundLocaleBox {
            val sound = args["sound"]!! as String
            val volume = args["volume"]
            val pitch = args["pitch"]
            return SoundLocaleBox(
                sound,
                (volume ?: 3F) as Float,
                (pitch ?: 0.5F) as Float,
            )
        }
    }
}


@SerializableAs("Title")
class TitleLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        TODO()
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): TitleLocaleBox {
            TODO()
        }
    }
}


@SerializableAs("Bar")
class BarLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): BarLocaleBox {
            TODO()
        }
    }
}

@SerializableAs("JSON")
class JSONLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        TODO()
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): JSONLocaleBox {
            TODO()
        }
    }
}

@SerializableAs("Action")
class ActionLocaleBox(
    val command: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        TODO()
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ActionLocaleBox {
            TODO()
        }
    }
}
