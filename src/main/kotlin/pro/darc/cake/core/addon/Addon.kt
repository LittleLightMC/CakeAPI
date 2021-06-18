package pro.darc.cake.core.addon

import pro.darc.cake.utils.Version

interface Addon {

    val name: String
    val version: Version

    fun onLoad()
    fun onEnable()
    fun onDisable()

}