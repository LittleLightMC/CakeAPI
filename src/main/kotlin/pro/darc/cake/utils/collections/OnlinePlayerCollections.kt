package pro.darc.cake.utils.collections

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import pro.darc.cake.module.extensions.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

typealias WhenPlayerQuitCollectionCallback = Player.() -> Unit
typealias WhenPlayerQuitMapCallback<V> = Player.(V) -> Unit

// List

fun Plugin.onlinePlayerListOf() = OnlinePlayerList()

fun Plugin.onlinePlayerListOf(vararg players: Player)
        = OnlinePlayerList().apply { addAll(players) }

fun onlinePlayerListOf(vararg pair: Pair<Player, WhenPlayerQuitCollectionCallback>)
        = OnlinePlayerList().apply { pair.forEach { (player, whenPlayerQuit) -> add(player, whenPlayerQuit) } }


// Set

fun Plugin.onlinePlayerSetOf() = OnlinePlayerSet(this)

fun onlinePlayerSetOf(vararg players: Player, plugin: Plugin)
        = OnlinePlayerSet(plugin).apply { addAll(players) }

fun Plugin.onlinePlayerSetOf(vararg players: Player)
        = onlinePlayerSetOf(*players, plugin = this)

fun onlinePlayerSetOf(vararg pair: Pair<Player, WhenPlayerQuitCollectionCallback>, plugin: Plugin)
        = OnlinePlayerSet(plugin).apply { pair.forEach { (player, whenPlayerQuit) -> add(player, whenPlayerQuit) } }

fun Plugin.onlinePlayerSetOf(vararg pair: Pair<Player, WhenPlayerQuitCollectionCallback>)
        = onlinePlayerSetOf(*pair, plugin = this)

// Map

fun <V> Plugin.onlinePlayerMapOf() = OnlinePlayerMap<V>()

fun <V> Plugin.onlinePlayerMapOf(vararg pair: Pair<Player, V>)
        = OnlinePlayerMap<V>().apply { putAll(pair) }

fun <V> onlinePlayerMapOf(vararg triple: Triple<Player, V, WhenPlayerQuitMapCallback<V>>)
        = OnlinePlayerMap<V>().apply { triple.forEach { (player, value, whenPlayerQuit) -> put(player, value, whenPlayerQuit) } }

class OnlinePlayerList() : LinkedList<Player>(), OnlinePlayerCollection, Plugin by cake {
    private val whenQuit: MutableList<Pair<Player, WhenPlayerQuitCollectionCallback>> = LinkedList()

    override fun add(player: Player, whenPlayerQuit: Player.() -> Unit): Boolean {
        return if(super<OnlinePlayerCollection>.add(player, whenPlayerQuit)) {
            whenQuit.add(player to whenPlayerQuit)
            true
        } else false
    }

    override fun add(element: Player): Boolean {
        if (isEmpty()) registerEvents(this)
        return super<LinkedList>.add(element)
    }

    override fun quit(player: Player): Boolean {
        return if(super.quit(player)) {
            val iterator = whenQuit.iterator()
            for (pair in iterator) {
                if(pair.first == player) {
                    iterator.remove()
                    pair.second.invoke(pair.first)
                }
            }
            true
        } else false
    }

    override fun removeFirst(): Player {
        return super.removeFirst().also {
            checkRegistration()
        }
    }

    override fun removeLastOccurrence(p0: Any?): Boolean {
        return super.removeLastOccurrence(p0).also {
            if(it) checkRegistration()
        }
    }

    override fun removeAt(p0: Int): Player {
        return super.removeAt(p0).also {
            checkRegistration()
        }
    }

    override fun remove(element: Player): Boolean {
        return if(super.remove(element)) {
            checkRegistration()
            true
        } else false
    }

    override fun removeLast(): Player {
        return super.removeLast().also {
            checkRegistration()
        }
    }
}

class OnlinePlayerSet(val plugin: Plugin) : HashSet<Player>(), OnlinePlayerCollection {
    private val whenQuit: MutableMap<Player, WhenPlayerQuitCollectionCallback> = mutableMapOf()

    override fun add(player: Player, whenPlayerQuit: WhenPlayerQuitCollectionCallback): Boolean {
        return if(super<OnlinePlayerCollection>.add(player, whenPlayerQuit)) {
            whenQuit[player] = whenPlayerQuit

            checkRegistration()
            true
        } else false
    }

    override fun add(element: Player): Boolean {
        return super<HashSet>.add(element).also {
            if(it) checkRegistration()
        }
    }

    override fun remove(element: Player): Boolean {
        return super.remove(element).also {
            if(it) checkRegistration()
        }
    }

    override fun quit(player: Player): Boolean {
        return if(super.quit(player)) {
            whenQuit.remove(player)?.also { block ->
                block.invoke(player)
            }
            true
        } else false
    }
}

interface OnlinePlayerCollection : MutableCollection<Player>, KListener {

    fun checkRegistration() {
        if(size == 1) {
            event<PlayerQuitEvent> { quit(player) }
            event<PlayerKickEvent> { quit(player) }
        } else if(size == 0) {
            unregisterListener()
        }
    }

    /**
     * Adds a new Player to the collection with a callback for when the player quits the server.
     */
    fun add(player: Player, whenPlayerQuit: WhenPlayerQuitCollectionCallback): Boolean {
        return add(player).also {
            if(it) checkRegistration()
        }
    }

    /**
     * Removes the player from the collection, calling the [WhenPlayerQuitCollectionCallback] provided.
     */
    fun quit(player: Player): Boolean {
        return remove(player).also {
            if(it) checkRegistration()
        }
    }

    /**
     * Clear the collection calling all [WhenPlayerQuitCollectionCallback] from the Players.
     */
    fun clearQuiting() {
        toMutableList().forEach {
            quit(it)
        }
    }
}

class OnlinePlayerMap<V> : HashMap<Player, V>(), KListener {
    private val whenQuit: HashMap<Player, WhenPlayerQuitMapCallback<V>> = hashMapOf()

    /**
     * Puts a Player to the map with a [value] and a callback for when the player quits the server.
     */
    fun put(key: Player, value: V, whenPlayerQuit: WhenPlayerQuitMapCallback<V>): V? {
        whenQuit[key] = whenPlayerQuit
        return put(key, value).also {
            checkRegistration()
        }
    }

    /**
     * Removes the player from the map, calling the [WhenPlayerQuitMapCallback] provided.
     */
    fun quit(player: Player) {
        remove(player)?.also {
            whenQuit.remove(player)?.also { block ->
                block.invoke(player, it)
            }
            checkRegistration()
        }
    }

    /**
     * Clear the map calling all [WhenPlayerQuitMapCallback] from the Players.
     */
    fun clearQuiting() {
        keys.toMutableList().forEach {
            quit(it)
        }
    }

    override fun remove(key: Player): V? {
        return super.remove(key).also {
            checkRegistration()
        }
    }

    override fun remove(key: Player, value: V): Boolean {
        return super.remove(key, value).also {
            checkRegistration()
        }
    }

    private fun checkRegistration() {
        if(size == 1) {
            event<PlayerQuitEvent> { quit(player) }
            event<PlayerKickEvent> { quit(player) }
        } else if(size == 0) {
            unregisterListener()
        }
    }
}