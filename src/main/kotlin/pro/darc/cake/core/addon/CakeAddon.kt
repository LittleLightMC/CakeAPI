package pro.darc.cake.core.addon

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

abstract class CakeAddon: Addon {

    override lateinit var dataFolder: File
    override lateinit var classLoader: ClassLoader
    override lateinit var name: String
    override var config: YamlConfiguration? = null

    override fun init() {
        dataFolder.mkdirs()
    }

    override fun getResource(filename: String): InputStream? {
        return try {
            classLoader.getResourceAsStream(filename)
        } catch (ex: IOException) {
            null
        }
    }

    fun saveDefaultConfig(replace: Boolean = false) {
        val file = File(dataFolder, "config.yml")
        if (file.exists() && !replace) return
        val inStream = getResource("config.yml") ?: return
        val outStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inStream.read(buf).also { len = it } > 0) {
            outStream.write(buf, 0, len)
        }
        outStream.close()
        inStream.close()
    }

    override fun saveDefaultAndGetConfig(): YamlConfiguration? {
        if (config != null) return config
        saveDefaultConfig(false)
        val file = File(dataFolder, "config.yml")
        return YamlConfiguration.loadConfiguration(file).apply {
            config = this
        }
    }

}
