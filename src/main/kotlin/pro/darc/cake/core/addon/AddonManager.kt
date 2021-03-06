package pro.darc.cake.core.addon

import org.bstats.charts.CustomChart
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import pro.darc.cake.CakeAPI
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.core.inject.LifecycleLoader
import pro.darc.cake.module.extensions.Log
import pro.darc.cake.module.extensions.cake
import pro.darc.cake.module.extensions.subFile
import pro.darc.cake.utils.*
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AddonManager : Plugin by CakeAPI.instance {

    private val addonMap = ConcurrentHashMap<String, AddonInfo>()

    private val addonFolder = cake.subFile("addons")
    private val initAddonFolder = Once {
        addonFolder.mkdirs()
    }

    private fun loadJar(file: File) {
        assert(file.isFile)
        val url = file.toURI().toURL()
        val loader = ChildFirstClassLoader(arrayOf(url), AddonManager::class.java.classLoader)
        val addonManifestStream = loader.getResourceAsStream("addon.yml")
        addonManifestStream?.let { addon ->
            val yamlConfiguration = YamlConfiguration.loadConfiguration(InputStreamReader(addon))
            val name = yamlConfiguration.getString("name")
            val version = yamlConfiguration.getString("version")
            val main = yamlConfiguration.getString("main")
            val uuid = UUID.fromString(yamlConfiguration.getString("id"))
            val info = AddonInfo(name!!, version!!, uuid, main!!)
            val mainClass = Class.forName(main, true, loader)
            info.instance = mainClass.getConstructor().newInstance() as Addon
            info.instance!!.dataFolder = File(addonFolder, "$name")
            info.instance!!.classLoader = loader
            info.instance!!.name = name
            addonMap[name] = info
            LifecycleLoader.addExternalLifecycle(mainClass.packageName, loader)
            info.instance!!.init()
            Log.info("Addon $name loaded successfully...")
        }
    }

    private val loadAddon = Once {
        val folder = cake.subFile("addons")
        assert(folder.exists())
        assert(folder.isDirectory)
        folder.listFiles { _, name ->
            name.endsWith(".jar")
        }?.forEach { file ->
            if (file.isFile) {
                try {
                    loadJar(file)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.warning("Error occurred while loading ${file.name} ...")
                }
            }
        }
    }

    fun getAddon(name: String): AddonInfo? = addonMap[name]

    @LifeInject([LifeCycle.CakeEnable, LifeCycle.CakeReload])
    @JvmStatic
    fun init() {
        addonMap.clear()
        initAddonFolder()
        loadAddon()
    }

}

class AddonInfo(val name: String, _version: String, val uuid: UUID, val main: String) {
    val version: Version = _version.toVersion()
    var instance: Addon? = null
        internal set
}

@Throws(IOException::class)
@Deprecated("This method not longer supported from Java 9 and above")
internal fun addURL(url: URL) {
    val sysClassLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
    try {
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(sysClassLoader, url)
    } catch (t: Throwable) {
        t.printStackTrace()
        throw IOException(t.localizedMessage)
    }
}
