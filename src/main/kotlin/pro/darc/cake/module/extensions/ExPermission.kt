package pro.darc.cake.module.extensions

import org.bukkit.permissions.Permissible

fun Permissible.anyPermission(vararg permission: String): Boolean = permission.any { hasPermission(it) }

fun Permissible.allPermission(vararg permissions: String): Boolean = permissions.all { hasPermission(it) }

fun Permissible.hasPermissionOrStar(permission: String): Boolean =
    hasPermission(permission) || hasPermission(permission.replaceAfterLast('.', "*"))
