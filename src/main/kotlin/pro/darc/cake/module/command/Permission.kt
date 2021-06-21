package pro.darc.cake.module.command

import pro.darc.cake.module.extensions.allPermission
import pro.darc.cake.module.extensions.anyPermission
import pro.darc.cake.module.extensions.hasPermissionOrStar

inline fun <reified T> Executor<*>.permission(
    permission: String, builder: () -> T
): T = permission({ sender.hasPermission(permission) }, builder)

inline fun <reified T> Executor<*>.permissionOrStar(
    permission: String, builder: () -> T
): T = permission({ sender.hasPermissionOrStar(permission) }, builder)

inline fun <reified T> Executor<*>.anyPermission(
    vararg permissions: String, builder: () -> T
): T = permission({ sender.anyPermission(*permissions) }, builder)

inline fun <reified T> Executor<*>.allPermission(
    vararg permissions: String, builder: () -> T
): T = permission({ sender.allPermission(*permissions) }, builder)

inline fun <reified T> Executor<*>.permission(
    permissionChecker: () -> Boolean,
    builder: () -> T
): T = if(permissionChecker()) builder() else fail(command.permissionMessage!!)
