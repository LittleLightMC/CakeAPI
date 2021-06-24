package pro.darc.cake.utils

import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.*


// From https://medium.com/@isuru89/java-a-child-first-class-loader-cbd9c3d0305
class ChildFirstClassLoader(urls: Array<URL?>?, parent: ClassLoader?) :
    URLClassLoader(urls, parent) {
    private val sysClzLoader: ClassLoader? = getSystemClassLoader()

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        // has the class loaded already?
        var loadedClass = findLoadedClass(name)
        if (loadedClass == null) {
            try {
                if (sysClzLoader != null) {
                    loadedClass = sysClzLoader.loadClass(name)
                }
            } catch (ex: ClassNotFoundException) {
                // class not found in system class loader... silently skipping
            }
            try {
                // find the class from given jar urls as in first constructor parameter.
                if (loadedClass == null) {
                    loadedClass = findClass(name)
                }
            } catch (e: ClassNotFoundException) {
                // class is not found in the given urls.
                // Let's try it in parent classloader.
                // If class is still not found, then this method will throw class not found ex.
                loadedClass = super.loadClass(name, resolve)
            }
        }
        if (resolve) {      // marked to resolve
            resolveClass(loadedClass)
        }
        return loadedClass
    }

    @Throws(IOException::class)
    override fun getResources(name: String): Enumeration<URL> {
        val allRes: MutableList<URL> = LinkedList<URL>()

        // load resources from sys class loader
        val sysResources: Enumeration<URL>? = sysClzLoader!!.getResources(name)
        if (sysResources != null) {
            while (sysResources.hasMoreElements()) {
                allRes.add(sysResources.nextElement())
            }
        }

        // load resource from this classloader
        val thisRes: Enumeration<URL>? = findResources(name)
        if (thisRes != null) {
            while (thisRes.hasMoreElements()) {
                allRes.add(thisRes.nextElement())
            }
        }

        // then try finding resources from parent classloaders
        val parentRes: Enumeration<URL>? = super.findResources(name)
        if (parentRes != null) {
            while (parentRes.hasMoreElements()) {
                allRes.add(parentRes.nextElement())
            }
        }
        return object : Enumeration<URL> {
            var it: Iterator<URL> = allRes.iterator()
            override fun hasMoreElements(): Boolean {
                return it.hasNext()
            }

            override fun nextElement(): URL {
                return it.next()
            }
        }
    }

    override fun getResource(name: String): URL? {
        var res: URL? = null
        if (sysClzLoader != null) {
            res = sysClzLoader.getResource(name)
        }
        if (res == null) {
            res = findResource(name)
        }
        if (res == null) {
            res = super.getResource(name)
        }
        return res
    }

}
