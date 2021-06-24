package pro.darc.cake.core.addon

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

abstract class CakeAddon: Addon {

    override lateinit var dataFolder: File
    override lateinit var uuid: UUID

    override fun init() {}

    override fun getResource(filename: String): InputStream? {
        return try {
            val url: URL = javaClass.classLoader.getResource(filename) ?: return null
            val connection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        } catch (ex: IOException) {
            null
        }
    }

}
