package pro.darc.cake.module.locale

import org.bukkit.command.CommandSender
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.Config
import pro.darc.cake.module.extensions.cake
import pro.darc.cake.module.extensions.migrateYAMLFile
import pro.darc.cake.module.extensions.subFile

object LocaleManager {

    const val languageDir = "lang"
    private lateinit var defaultLangUnit: LocaleUnit

    init {
        init()
    }

    @LifeInject([LifeCycle.CakeReload])
    @JvmStatic
    fun init() {
        cake.subFile(languageDir).mkdirs()
        val filename ="$languageDir/${Config.main.getString("default language")!!}.yml"
        defaultLangUnit = cake.migrateYAMLFile(filename).asLocale()
    }

    fun sendToDefault(receiver: CommandSender, key: String) {
        defaultLangUnit.sendTo(receiver, key)
    }

}

fun CommandSender.sendDefaultLocale(key: String) {
    LocaleManager.sendToDefault(this, key)
}
