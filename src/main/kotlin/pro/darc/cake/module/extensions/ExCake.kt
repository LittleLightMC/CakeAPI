package pro.darc.cake.module.extensions

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import pro.darc.cake.CakeAPI
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

val cake get() = CakeAPI.instance

fun Plugin.subFile(name: String): File = File(this.dataFolder, name)


/**
 * Release resource file if not exist
 *
 * @param path path to file in jar
 */
fun Plugin.saveDefaultResourceByPath(path: String) {
    Config.saveResource(path, false)
}


fun Plugin.migrateYAMLFile(path: String): YamlConfiguration {
    val packedStream = getResource(path)
        ?: throw FileNotFoundException("Could not found $path in jar resources.")
    val file = subFile(path)
    val packedYaml = YamlConfiguration.loadConfiguration(InputStreamReader(packedStream))
    if (file.exists()) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        var flag = false
        for (defaultKey in packedYaml.getKeys(true)) {
            if (!yaml.contains(defaultKey)) {
                yaml.set(defaultKey, packedYaml.get(defaultKey))
                flag = true
            }
        }
        if (flag) yaml.save(file)
    }
    return YamlConfiguration.loadConfiguration(file)
}


object Config: Plugin by cake {
    lateinit var main: YamlConfiguration
    lateinit var db: YamlConfiguration

    @LifeInject([LifeCycle.CakeReload, LifeCycle.CakeLoad])
    @JvmStatic
    fun init() {
        main = migrateYAMLFile("config.yml")
        db = migrateYAMLFile("db.yml")
    }
}
