package pro.darc.cake.module.menu.dsl

import org.bukkit.inventory.ItemStack
import pro.darc.cake.module.menu.Menu
import pro.darc.cake.module.menu.calculateSlot
import pro.darc.cake.module.menu.dsl.slot.SlotDSL

inline fun MenuDSL.slot(
    line: Int,
    slot: Int,
    item: ItemStack?,
    block: SlotDSL.() -> Unit = {}
): SlotDSL = slot(calculateSlot(line, slot), item, block)

inline fun MenuDSL.slot(
    slot: Int,
    item: ItemStack?,
    block: SlotDSL.() -> Unit = {}
): SlotDSL = baseSlot.clone(item).apply(block).also {
    setSlot(slot, it)
}

interface MenuDSL : Menu<SlotDSL> {

    override val eventHandler: MenuEventHandlerDSL

    fun onUpdate(update: MenuPlayerUpdateEvent) {
        eventHandler.updateCallbacks.add(update)
    }

    fun onClose(close: MenuPlayerCloseEvent) {
        eventHandler.closeCallbacks.add(close)
    }

    fun onMoveToMenu(moveToMenu: MenuPlayerMoveToEvent) {
        eventHandler.moveToMenuCallbacks.add(moveToMenu)
    }

    fun preOpen(preOpen: MenuPlayerPreOpenEvent) {
        eventHandler.preOpenCallbacks.add(preOpen)
    }

    fun onOpen(open: MenuPlayerOpenEvent) {
        eventHandler.openCallbacks.add(open)
    }

}
