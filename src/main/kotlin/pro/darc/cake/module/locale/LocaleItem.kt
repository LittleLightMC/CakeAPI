package pro.darc.cake.module.locale

import org.bukkit.configuration.file.YamlConfiguration
import pro.darc.cake.module.extensions.colorize
import pro.darc.cake.module.extensions.then

/**
 * Cast yaml to a locale provider instance
 */
fun YamlConfiguration.asLocale() {
    val res = mutableMapOf<String, LocaleBox>()
    getKeys(true).forEach { key ->
        isString(key) then {
            res[key] = SimpleStringBox(getString(key)!!.colorize())
        }
    }
}
