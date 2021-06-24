package pro.darc.cake.module.menu.dsl.pagination

import pro.darc.cake.module.menu.MenuPlayer

typealias MenuPlayerPageChangeEvent = MenuPlayer.() -> Unit
typealias MenuPlayerPageAvailableEvent = MenuPlayer.() -> Unit

class PaginationEventHandler {
    val pageChangeCallbacks = mutableListOf<MenuPlayerPageChangeEvent>()
    val pageAvailableCallbacks = mutableListOf<MenuPlayerPageAvailableEvent>()

    fun pageChange(pageChange: MenuPlayer) {
        for (callback in pageChangeCallbacks) {
            callback(pageChange)
        }
    }

    fun pageAvailable(pageAvailable: MenuPlayer) {
        for (callback in pageAvailableCallbacks) {
            callback(pageAvailable)
        }
    }
}
