package pro.darc.cake.module.locale

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.okkero.skedule.schedule
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.configuration.serialization.SerializableAs
import org.bukkit.entity.Player
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.*
import java.util.*
import kotlin.reflect.KClass

interface LocaleBox {
    fun send(receiver: CommandSender)
}

class MissingFieldError(override val message: String? = "", override val cause: Throwable? = null): RuntimeException()

@Throws(MissingFieldError::class)
fun<T> REQUIRED(item: T?): T {
    return (item ?: throw MissingFieldError())
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
    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            this.performCommand(command.replaceByPAPI(this))
        }
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
        } otherwise {
            sendMessage("Played sound $sound with $volume dB in $pitch")
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
            val sound = REQUIRED(args["sound"] as String)
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
    val title: String,
    private var subtitle: String?,
    private var fadein: Int?,
    private var fadeout: Int?,
    private var stay: Int?,
): ComplexLocaleBox() {

    init {
        this.subtitle = subtitle ?: ""
        this.fadein = fadein ?: 0
        this.fadeout = fadeout ?: 0
        this.stay = stay ?: 3
    }

    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            val title = title.colorize().replaceByPAPI(this)
            sendTitle(title, subtitle, fadein!!, stay!!, fadeout!!)
        } otherwise {
            sendMessage(title.colorize())
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "title" to title,
            "subtitle" to subtitle!!,
            "fadein" to fadein!!,
            "fadeuot" to fadeout!!,
            "stay" to stay!!,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): TitleLocaleBox {
            return TitleLocaleBox(
                title = REQUIRED(args["title"] as String),
                subtitle = args["subtitle"] as String?,
                fadein = args["fadein"] as Int?,
                fadeout = args["fadeout"] as Int?,
                stay = args["stay"] as Int?,
            )
        }
    }
}


@SerializableAs("Bar")
class BarLocaleBox(
    val text: String,
    private var color: String?,
    private var style: String?,
    private var progress: Float?,
    private var timeout: Int?,
    private var interval: Int?,
): ComplexLocaleBox() {

    init {
        this.color = color?.uppercase() ?: "BLUE"
        this.style = style?.uppercase() ?: "SEGMENTED_20"
        this.progress = progress ?: 1F
        this.timeout = timeout ?: 20
        this.interval = interval ?: 2
    }

    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            val uuid: UUID = UUID.randomUUID()
            var prog = progress
            val title = text.colorize().replaceByPAPI(this)
            sendPacket {
                val container = PacketContainer(PacketType.Play.Server.BOSS)
                container.uuiDs.write(0, uuid)
                container.integers.write(0, 0) // add
                container.chatComponents.write(0, WrappedChatComponent.fromText(title))
                container.float.write(0, prog)
                container.integers.write(1, BarColor.valueOf(color!!).ordinal) // color
                container.integers.write(2, BarStyle.valueOf(style!!).ordinal) // division
                container
            }
            scheduler.schedule(cake) {
                for (i in 0..timeout!!) {
                    waitFor((20 * interval!!).toLong())
                    prog = prog!!.minus(progress!! / (timeout!! * interval!!))
                    sendPacket {
                        val container = PacketContainer(PacketType.Play.Server.BOSS)
                        container.uuiDs.write(0, uuid)
                        container.integers.write(0, 2) // update health
                        container.float.write(0, prog)
                        container
                    }
                }
                sendPacket {
                    val container = PacketContainer(PacketType.Play.Server.BOSS)
                    container.uuiDs.write(0, uuid)
                    container
                }
            }
        } otherwise {
            sendMessage(text.colorize())
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "text" to text,
            "color" to color!!,
            "style" to style!!,
            "progress" to progress!!,
            "timeout" to timeout!!,
            "interval" to interval!!,
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): BarLocaleBox {
            return BarLocaleBox(
                text = REQUIRED(args["text"] as String),
                color = args["color"] as String?,
                style = args["style"] as String?,
                progress = args["progress"] as Float?,
                timeout = args["timeout"] as Int?,
                interval = args["interval"] as Int?,
            )
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
    val text: String,
): ComplexLocaleBox() {
    override fun send(receiver: CommandSender) {
        var message = text.colorize()
        receiver.isPlayerThen {
            message = message.replaceByPAPI(this)
            spigot().sendMessage(ChatMessageType.ACTION_BAR, textOf(message))
        } otherwise {
            sendMessage(message)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ActionLocaleBox {
            return ActionLocaleBox(REQUIRED(args["text"] as String))
        }
    }
}
