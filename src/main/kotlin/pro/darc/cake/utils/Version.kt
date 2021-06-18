package pro.darc.cake.utils

class Version(
    private val major: Int,
    private val minor: Int,
    private val patch: Int,
    private val pre: String = "",
    private val meta: String = ""
) {
    override fun toString(): String {
        var text = "$major.$minor.$patch"
        if (pre.isBlank() or meta.isBlank()) {
            text += "($pre$meta)"
        }
        return text
    }

    fun compareTo(target: Version): Int {
        return (major * 10000 + minor * 1000 + patch * 100) - (target.major * 10000 + target.minor * 1000 + target.patch * 100)
    }

}

/**
 * regex expression to split the version
 */
val versionRegex =
    Regex("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(-[a-zA-Z\\d][-a-zA-Z.\\d]*)?(\\+[a-zA-Z\\d][-a-zA-Z.\\d]*)?\$")

fun String.toVersion(): Version {
    val (major, minor, patch, pre, meta) = versionRegex.find(this, 0)!!.destructured
    return Version(major.toInt(), minor.toInt(), patch.toInt(), pre, meta)
}
