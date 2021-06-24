package pro.darc.cake.core.addon

import java.io.File
import java.io.InputStream

interface Addon {

    var dataFolder: File

    fun init()
    fun getResource(filename: String): InputStream?

}