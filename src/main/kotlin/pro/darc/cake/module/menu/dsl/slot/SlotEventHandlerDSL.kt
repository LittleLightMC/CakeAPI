package pro.darc.cake.module.menu.dsl.slot

import pro.darc.cake.module.menu.slot.*

typealias MenuPlayerSlotInteractEvent = MenuPlayerSlotInteract.() -> Unit
typealias MenuPlayerSlotRenderEvent = MenuPlayerSlotRender.() -> Unit
typealias MenuPlayerSlotUpdateEvent = MenuPlayerSlotUpdate.() -> Unit
typealias MenuPlayerSlotMoveToEvent = MenuPlayerSlotMoveTo.() -> Unit

class SlotEventHandlerDSL : SlotEventHandler {

    val interactCallbacks = mutableListOf<MenuPlayerSlotInteractEvent>()
    val renderCallbacks = mutableListOf<MenuPlayerSlotRenderEvent>()
    val updateCallbacks = mutableListOf<MenuPlayerSlotUpdateEvent>()
    val moveToSlotCallbacks = mutableListOf<MenuPlayerSlotMoveToEvent>()

    override fun interact(interact: MenuPlayerSlotInteract) {
        for (callback in interactCallbacks) {
            callback(interact)
        }
    }

    override fun render(render: MenuPlayerSlotRender) {
        for (callback in renderCallbacks) {
            callback(render)
        }
    }

    override fun update(update: MenuPlayerSlotUpdate) {
        for (callback in updateCallbacks) {
            callback(update)
        }
    }

    override fun moveToSlot(moveToSlot: MenuPlayerSlotMoveTo) {
        for (callback in moveToSlotCallbacks) {
            callback(moveToSlot)
        }
    }

    override fun clone(): SlotEventHandlerDSL {
        return SlotEventHandlerDSL().also {
            it.interactCallbacks.addAll(interactCallbacks)
            it.renderCallbacks.addAll(renderCallbacks)
            it.updateCallbacks.addAll(updateCallbacks)
            it.moveToSlotCallbacks.addAll(moveToSlotCallbacks)
        }
    }

}
