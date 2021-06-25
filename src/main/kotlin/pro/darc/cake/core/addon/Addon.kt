package pro.darc.cake.core.addon

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.util.*

interface Addon {

    var dataFolder: File
    var classLoader: ClassLoader
    var name: String
    var config: YamlConfiguration?

    fun init()

    /**
     * Getting resource from addon's jar as InputStream
     *
     * @param filename path to file, could be relative
     */
    fun getResource(filename: String): InputStream?

    /**
     * Save config.yml to dataFolder and return YAMLConfiguration
     *
     * @return nullable if resource don't exist
     */
    fun saveDefaultAndGetConfig(): YamlConfiguration?

}