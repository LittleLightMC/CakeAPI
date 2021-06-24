package pro.darc.cake.module.menu.dsl.slot

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pro.darc.cake.module.menu.dsl.MenuDSL
import java.util.*

fun MenuDSL.newSlot(item: ItemStack?, builder: SlotDSL.() -> Unit): SlotDSL {
    return SlotDSLImpl(item, cancelOnClick).apply(builder)
}

class SlotDSLImpl(
    override val item: ItemStack?,
    override var cancel: Boolean,
    override val eventHandler: SlotEventHandlerDSL = SlotEventHandlerDSL()
) : SlotDSL {

    override val slotData = WeakHashMap<String, Any>()
    override val playerSlotData = WeakHashMap<Player, WeakHashMap<String, Any>>()

    override fun clone(item: ItemStack?): SlotDSL = SlotDSLImpl(item, cancel, eventHandler.clone())

}
