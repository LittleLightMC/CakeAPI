package pro.darc.cake.module.menu.dsl.slot

import org.bukkit.inventory.ItemStack
import pro.darc.cake.module.menu.slot.Slot

interface SlotDSL : Slot {

    override val eventHandler: SlotEventHandlerDSL

    fun onClick(click: MenuPlayerSlotInteractEvent) {
        eventHandler.interactCallbacks.add(click)
    }

    fun onRender(render: MenuPlayerSlotRenderEvent) {
        eventHandler.renderCallbacks.add(render)
    }

    fun onUpdate(update: MenuPlayerSlotUpdateEvent) {
        eventHandler.updateCallbacks.add(update)
    }

    fun onMoveToSlot(moveToSlot: MenuPlayerSlotMoveToEvent) {
        eventHandler.moveToSlotCallbacks.add(moveToSlot)
    }

    override fun clone(item: ItemStack?): SlotDSL
}
