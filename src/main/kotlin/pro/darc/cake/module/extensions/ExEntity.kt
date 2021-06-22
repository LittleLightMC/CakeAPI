package pro.darc.cake.module.extensions

import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.util.Vector
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Entity.isPlayer(): Boolean {
    contract { returns(true) implies (this@isPlayer is Player) }

    return type == EntityType.PLAYER
}

//  firework

inline fun firework(location: Location, block: FireworkMeta.() -> Unit): Firework {
    return location.spawn<Firework>().apply { meta(block) }
}

inline fun Firework.meta(block: FireworkMeta.() -> Unit) = apply {
    fireworkMeta = fireworkMeta.apply(block)
}

inline fun <reified T : Projectile> ProjectileSource.launchProjectile() = launchProjectile(T::class.java)
inline fun <reified T : Projectile> ProjectileSource.launchProjectile(vector: Vector) = launchProjectile(T::class.java, vector)
