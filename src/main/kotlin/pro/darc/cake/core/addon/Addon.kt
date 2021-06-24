package pro.darc.cake.core.addon

import java.io.File
import java.io.InputStream
import java.util.*

interface Addon {

    var dataFolder: File
    var uuid: UUID
    var classLoader: ClassLoader

    fun init()
    fun getResource(filename: String): InputStream?

}