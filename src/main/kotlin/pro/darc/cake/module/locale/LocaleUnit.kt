package pro.darc.cake.module.locale

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import pro.darc.cake.module.extensions.colorize
import pro.darc.cake.module.extensions.then
import java.util.concurrent.ConcurrentHashMap

class MissingLocaleItemException(
    override val message: String?
): RuntimeException(message)

class LocaleUnit private constructor() {
    private val itemMap: ConcurrentHashMap<String, LocaleBox> by lazy { ConcurrentHashMap() }

    fun sendTo(receiver: CommandSender, key: String) {
        val box = itemMap[key] ?: throw MissingLocaleItemException("Missing item \"$key\".")
        box.send(receiver)
    }

    fun asString(key: String): String? {
        return itemMap[key]?.toString()
    }

    companion object {
        fun fromMutableMap(map: MutableMap<String, LocaleBox>): LocaleUnit {
            val res = LocaleUnit()
            map.forEach { (key, box) ->
                res.itemMap[key] = box
            }
            return res
        }
    }
}

/**
 * Cast yaml to a locale provider instance
 */
fun YamlConfiguration.asLocale(): LocaleUnit {
    val res = mutableMapOf<String, LocaleBox>()
    getKeys(true).forEach { key ->
        (!isConfigurationSection(key)) then {
            res[key] = when {
                isString(key) -> {
                    TextLocaleBox(getString(key)!!)
                }
                isList(key) -> {
                    val list = getList(key)!!.filterIsInstance<LocaleBox>() as ArrayList<LocaleBox>
                    LocaleArray(list)
                }
                else -> {
                    // is object
                    get(key).takeIf { it is LocaleBox } as LocaleBox
                }
            }
        }
    }
    return LocaleUnit.fromMutableMap(res)
}
