package pro.darc.cake.module.locale

import com.okkero.skedule.schedule
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.lang.text.StrSubstitutor
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
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
import java.util.regex.Pattern
import kotlin.reflect.KClass

interface LocaleBox {
    fun send(receiver: CommandSender)
}

class MissingFieldError(override val message: String? = "", override val cause: Throwable? = null) :
    RuntimeException(message, cause)

@Throws(MissingFieldError::class)
fun <T> REQUIRED(item: T?): T {
    return (item ?: throw MissingFieldError())
}

class SimpleStringBox(val string: String) : LocaleBox {
    override fun send(receiver: CommandSender) {
        receiver.sendMessage(string.colorize())
    }

}

abstract class ComplexLocaleBox : LocaleBox, ConfigurationSerializable {
    override fun toString(): String {
        return "[LocalBox]"
    }
}

interface StringAble

fun StringAble.asString() = this.toString()

fun StringAble.format(map: Map<String, String>): String {
    val sub = StrSubstitutor(map, "%(", ")")
    return sub.replace(this)
}

object Box {
    private val boxList: List<KClass<out ComplexLocaleBox>> = listOf(
        TextLocaleBox::class, PlayerCommandLocaleBox::class,
        ServerCommandLocaleBox::class, SoundLocaleBox::class,
        TitleLocaleBox::class, BarLocaleBox::class,
        ActionLocaleBox::class, XMLLocaleBox::class,
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
) : ComplexLocaleBox(), StringAble {
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
            return TextLocaleBox(REQUIRED(args["text"]) as String)
        }
    }

    override fun toString(): String {
        return this.text.colorize()
    }

}

@SerializableAs("PlayerCommand")
class PlayerCommandLocaleBox(
    val command: String,
) : ComplexLocaleBox() {
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
            return PlayerCommandLocaleBox(REQUIRED(args["command"]) as String)
        }
    }
}


@SerializableAs("ServerCommand")
class ServerCommandLocaleBox(
    val command: String,
) : ComplexLocaleBox() {
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
            return PlayerCommandLocaleBox(REQUIRED(args["command"]) as String)
        }
    }
}


@SerializableAs("Sound")
class SoundLocaleBox(
    private var sound: String,
    val volume: Float = 3F,
    val pitch: Float = 0.5F,
) : ComplexLocaleBox() {

    init {
        this.sound = sound.lowercase() // resource key must follow regex [a-z0-9/._-]
    }

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
            val sound = REQUIRED(args["sound"]) as String
            val volume = args["volume"]
            val pitch = args["pitch"]
            return SoundLocaleBox(
                sound,
                ((volume ?: 3.0) as Double).toFloat(),
                ((pitch ?: 0.5) as Double).toFloat(),
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
) : ComplexLocaleBox(), StringAble {

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

    override fun toString(): String {
        return this.title.colorize()
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): TitleLocaleBox {
            return TitleLocaleBox(
                title = REQUIRED(args["title"]) as String,
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
    private var progress: Double?,
    private var timeout: Int?,
    private var interval: Int?,
    private var flags: ArrayList<String>?,
) : ComplexLocaleBox(), StringAble {

    val _flags: Array<BarFlag> by lazy {
        flags!!.map {
            BarFlag.valueOf(it)
        }.toTypedArray()
    }

    init {
        this.color = color?.uppercase() ?: "BLUE"
        this.style = style?.uppercase() ?: "SEGMENTED_20"
        this.progress = progress ?: 1.0
        this.timeout = timeout ?: 20
        this.interval = interval ?: 2
        this.flags = flags ?: arrayListOf()
    }

    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            var prog = progress!!
            val title = text.colorize().replaceByPAPI(this)
            val bossbar = Bukkit.createBossBar(title, BarColor.valueOf(color!!), BarStyle.valueOf(style!!), *_flags)
            bossbar.progress = prog
            bossbar.addPlayer(this)
            scheduler.schedule(cake) {
                for (i in 0..timeout!! step interval!!) {
                    waitFor((interval!!).toLong())
                    prog = prog.minus(progress!! / (timeout!! / interval!!))
                    if (prog >= 0) bossbar.progress = prog
                }
                bossbar.removeAll()
                bossbar.isVisible = false
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
            "flags" to flags!!,
        )
    }

    override fun toString(): String {
        return text.colorize()
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): BarLocaleBox {
            return BarLocaleBox(
                text = REQUIRED(args["text"]) as String,
                color = args["color"] as String?,
                style = args["style"] as String?,
                progress = args["progress"] as Double?,
                timeout = args["timeout"] as Int?,
                interval = args["interval"] as Int?,
                flags = args["flags"] as ArrayList<String>?,
            )
        }
    }
}

@Deprecated("Use XMLLocaleBox instead.")
@SerializableAs("JSON")
class JSONLocaleBox private constructor() : ComplexLocaleBox() {

    lateinit var component: List<BaseComponent>
    lateinit var textRaw: List<TextComponent>

    override fun send(receiver: CommandSender) {
        receiver.isPlayerThen {
            this.msg(concat(this))
        } otherwise {
            this.msg(concat(null))
        }
    }

    private fun concat(player: Player?): List<BaseComponent> {
        val count = component.size.coerceAtMost(textRaw.size)
        val res = mutableListOf<BaseComponent>()
        for (i in 0 until count) {
            textRaw[i].text = textRaw[i].text.replaceByPAPI(player)
            res.add(textRaw[i])
            res.add(component[i])
        }
        if (textRaw.size >= count) res.addAll(textRaw.subList(count, textRaw.size))
        return res
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
        )
    }

    companion object {
        val pattern: Pattern = Pattern.compile("<(?<text>[^<>]+)?@(?<node>[^<>]+)>")

        @JvmStatic
        fun deserialize(args: Map<String, Any>): JSONLocaleBox {
            val res = JSONLocaleBox()
            val text = (REQUIRED(args["text"]) as String).colorize()
            res.textRaw = text.split(pattern).map { textOf(it) }
            val resList = mutableListOf<BaseComponent>()
            (args["args"] is Map<*, *>) then {
                val nodes = (args["args"]!! as Map<*, *>).mapKeys {
                    it.key.toString()
                }
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    val t = matcher.group("text").colorize()
                    val n = matcher.group("node")
                    if (nodes.containsKey(n)) {
                        val comp = TextComponent.fromLegacyText(t)
                        nodes.containsKeyIgnoreCase("suggest") then {
                            Arrays.stream(comp).forEach { bc ->
                                bc.suggestCommand(nodes.getIgnoreCase("suggest").toString().colorize())
                            }
                        }
                        nodes.containsKeyIgnoreCase("command") then {
                            Arrays.stream(comp).forEach { bc ->
                                bc.runCommand(nodes.getIgnoreCase("command").toString().colorize())
                            }
                        }
                        nodes.containsKeyIgnoreCase("url") then {
                            Arrays.stream(comp).forEach { bc ->
                                bc.openUrl(nodes.getIgnoreCase("url").toString().colorize())
                            }
                        }
                        nodes.containsKeyIgnoreCase("hover") then {
                            Arrays.stream(comp).forEach { bc ->
                                bc.showText(textOf(nodes.getIgnoreCase("hover").toString().colorize()))
                            }
                        }
                        nodes.containsKeyIgnoreCase("insertion") then {
                            Arrays.stream(comp).forEach { bc ->
                                bc.insertion = nodes.getIgnoreCase("insertion").toString().colorize()
                            }
                        }
                        resList.addAll(comp.asList())
                    } else {
                        resList.add(textOf(t))
                    }
                }
            }
            res.component = resList
            return res
        }
    }
}

@SerializableAs("XML")
class XMLLocaleBox(
    val text: String,
): ComplexLocaleBox(), StringAble {
    override fun send(receiver: CommandSender) {
        val replaced = text.replaceByPAPI(if(receiver is Player) receiver else null)
        receiver.msg(MiniMessage.get().parse(replaced))
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "text" to text,
        )
    }

    override fun toString(): String {
        return text.replaceByPAPI(null)
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): XMLLocaleBox {
            return XMLLocaleBox(REQUIRED(args["text"]) as String)
        }
    }

}

@SerializableAs("Action")
class ActionLocaleBox(
    val text: String,
) : ComplexLocaleBox(), StringAble {
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

    override fun toString(): String {
        return text.colorize()
    }

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ActionLocaleBox {
            return ActionLocaleBox(REQUIRED(args["text"]) as String)
        }
    }
}

class LocaleArray(
    private val boxes: List<LocaleBox>,
) : LocaleBox, StringAble{

    override fun send(receiver: CommandSender) {
        this.boxes.forEach {
            it.send(receiver)
        }
    }

    override fun toString(): String {
        return boxes.find { it is StringAble }?.toString() ?: "[LocaleArray@${boxes.size}]"
    }

}
