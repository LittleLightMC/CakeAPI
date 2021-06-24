package pro.darc.cake.module.command.arguments

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.material.MaterialData
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.TabCompleter
import pro.darc.cake.module.command.fail
import pro.darc.cake.module.extensions.asBlockData
import pro.darc.cake.module.extensions.asMaterialData
import pro.darc.cake.module.extensions.color
import pro.darc.cake.module.extensions.textOf
import pro.darc.cake.module.locale.LocaleManager
import java.util.*

// MATERIAL

val MATERIAL_NOT_FOUND = textOf(LocaleManager.asStringDefault("error notfound block arg")!!)
val MATERIAL_MISSING_PARAMETER = textOf(LocaleManager.asStringDefault("error missing block arg")!!)

private fun toMaterial(string: String) = Material.getMaterial(string.uppercase(Locale.getDefault()))

/**
 * Returns [Material] or null if the Material was not found.
 */
fun Executor<*>.materialOrNull(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER
): Material? = string(index, argMissing).run {
    toMaterial(this)
}

fun Executor<*>.material(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
    notFound: BaseComponent = MATERIAL_NOT_FOUND
): Material = materialOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.material(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Material.values().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name.lowercase(Locale.getDefault()) else null
    }
}

// MATERIAL DATA

val DATA_FORMAT = textOf(LocaleManager.asStringDefault("error block data must number")!!)

fun Executor<*>.blockDataOrNull(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
): BlockData? = string(index, argMissing).run {
    val sliced = this.split(':')
    sliced.getOrNull(1)?.run {
        (toMaterial(sliced[0]))
            ?.asBlockData(this)
    } ?: materialOrNull(index, argMissing)?.asBlockData()
}

fun Executor<*>.blockData(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
    notFound: BaseComponent = MATERIAL_NOT_FOUND,
    dataFormat: BaseComponent = DATA_FORMAT,
): BlockData = blockDataOrNull(index, argMissing) ?: fail(notFound)

/**
 * Returns [MaterialData] or null if the Material was not found.
 */
fun Executor<*>.materialDataOrNull(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
    dataFormat: BaseComponent = DATA_FORMAT
): MaterialData? = string(index, argMissing).run {
    val sliced = this.split(":")
    sliced.getOrNull(1)?.run {
        (toMaterial(sliced[0]))
            ?.asMaterialData(toIntOrNull()?.toByte() ?: fail(dataFormat))
    } ?: materialOrNull(index, argMissing)?.asMaterialData()
}

fun Executor<*>.materialData(
    index: Int,
    argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
    notFound: BaseComponent = MATERIAL_NOT_FOUND,
    dataFormat: BaseComponent = DATA_FORMAT
): MaterialData = materialDataOrNull(index, argMissing, dataFormat) ?: fail(notFound)