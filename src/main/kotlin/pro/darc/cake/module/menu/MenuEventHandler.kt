package pro.darc.cake.module.menu

interface MenuEventHandler {

    fun update(update: MenuPlayerUpdate)

    fun close(close: MenuPlayerClose)

    fun moveToMenu(moveToMenu: MenuPlayerMoveTo)

    fun preOpen(preOpen: MenuPlayerPreOpen)

    fun open(open: MenuPlayerOpen)

}
