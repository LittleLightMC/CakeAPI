package pro.darc.cake.module.extensions

import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.clearArmor() {
    setArmorContents(arrayOf<ItemStack?>(null, null, null, null))
}

fun PlayerInventory.clearAll() {
    clear()
    clearArmor()
}

val Player.hasItemInMainHand get() = inventory.itemInMainHand.type != Material.AIR
val Player.hasItemInOffHand get() = inventory.itemInOffHand.type != Material.AIR
val Player.hasItemInHand get() = hasItemInMainHand && hasItemInOffHand

fun Player.playSound(sound: Sound, volume: Float, pitch: Float) = playSound(location, sound, volume, pitch)
fun Player.playNote(instrument: Instrument, note: Note) = playNote(location, instrument, note)
fun <T> Player.playEffect(effect: Effect, data: T? = null) = playEffect(player!!.location, effect, data)

fun CommandSender.msg(message: List<String>) = message.forEach { msg(it) }

fun Player.resetWalkSpeed() {
    player!!.walkSpeed = 0.2f
}

fun Player.resetFlySpeed() {
    player!!.flySpeed = 0.1f
}

fun CommandSender.isPlayer(): Boolean {
    return this is Player
}

class ElseContext<T>(private val boxed: T?) {
    infix fun otherwise(callback: T.() -> Unit) = boxed?.let { callback(it) }
}

fun CommandSender.isPlayerThen(then: Player.() -> Unit): ElseContext<CommandSender> {
    return if (isPlayer()) {
        then(this as Player)
        ElseContext(null)
    }
    else ElseContext(this)
}
