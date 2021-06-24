package pro.darc.cake.core.addon

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

abstract class CakeAddon: Addon {

    override lateinit var dataFolder: File
    override lateinit var uuid: UUID
    override lateinit var classLoader: ClassLoader

    override fun init() {}

    override fun getResource(filename: String): InputStream? {
        return try {
            classLoader.getResourceAsStream(filename)
        } catch (ex: IOException) {
            null
        }
    }

}
