package pro.darc.cake.module.locale

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import pro.darc.cake.core.addon.Addon
import pro.darc.cake.core.addon.AddonManager
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.Config
import pro.darc.cake.module.extensions.cake
import pro.darc.cake.module.extensions.migrateYAMLFile
import pro.darc.cake.module.extensions.subFile
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object LocaleManager {

    const val languageDir = "lang"
    private lateinit var defaultLangUnit: LocaleUnit

    private val cachedUnit: MutableMap<String, LocaleUnit> by lazy {
        ConcurrentHashMap()
    }

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

    fun asStringDefault(key: String): String {
        return defaultLangUnit.asString(key) ?: ""
    }

    /**
     * Getting YAML resources in "lang/" dir pf Addon's jar as LocalUnit.
     *
     * The LocalUnit will be cached in ConcurrentHashMap.
     *
     * @param addonId addon's uuid
     * @param language language key
     */
    fun getAddonLocale(addonId: UUID, language: String): LocaleUnit? {
        val key = "$addonId-$language"
        val cached = cachedUnit[key]
        if (cached != null) return cached

        val addonInfo = AddonManager.getAddon(addonId) ?: return null
        val path = "lang/$language.yml"
        val stream = addonInfo.instance?.getResource(path) ?: return null
        val reader = InputStreamReader(stream, "UTF-8")
        return YamlConfiguration.loadConfiguration(reader).asLocale().apply {
            cachedUnit[key] = this
        }
    }

}

fun CommandSender.sendDefaultLocale(key: String) {
    LocaleManager.sendToDefault(this, key)
}

fun Addon.getLocale(language: String): LocaleUnit? = LocaleManager.getAddonLocale(uuid, language)
fun Addon.getDefaultLocale(): LocaleUnit? = LocaleManager.getAddonLocale(uuid, Config.main.getString("default language")!!)

