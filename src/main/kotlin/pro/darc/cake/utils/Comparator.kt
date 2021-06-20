package pro.darc.cake.utils

import org.bukkit.entity.Player


class HashcodeComparator<T> : Comparator<T> {
    override fun compare(p0: T, p1: T): Int {
        return p0.hashCode().compareTo(p1.hashCode())
    }
}

object PlayerComparator : Comparator<Player> {
    override fun compare(p0: Player, p1: Player): Int {
        return p0.uniqueId.compareTo(p1.uniqueId)
    }
}
