package pro.darc.cake.core.inject

import org.reflections8.Reflections
import org.reflections8.scanners.MethodAnnotationsScanner
import org.reflections8.util.ClasspathHelper
import org.reflections8.util.ConfigurationBuilder
import pro.darc.cake.CakeAPI
import pro.darc.cake.utils.Priority
import pro.darc.cake.utils.TaggedConcurrentRunnablePool
import java.lang.reflect.Method
import java.lang.reflect.Modifier

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LifeInject(val type: Array<LifeCycle>, val priority: Priority = Priority.LOW)

enum class LifeCycle {
    CakeLoad,
    CakeEnable,
    CakeDisable,
    CakeReload,
    CakeTest,
}

private fun scanInnerPackageLifeCycle(): Set<Method>? {
    val pkgName = CakeAPI::class.java.packageName
    val reflect = Reflections(
        ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(pkgName))
            .setScanners(MethodAnnotationsScanner())
    )
    return reflect.getMethodsAnnotatedWith(LifeInject::class.java)
}

/**
 * Get lifecycle annotated with LifeInject
 *
 * @param pkgName package path
 * @param loaders ClassLoader(s)
 */
fun scanPackageLifecycle(pkgName: String, vararg loaders: ClassLoader): List<Method> {
    val reflect = Reflections(
        ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(pkgName, *loaders))
            .addClassLoaders(*loaders)
            .setScanners(MethodAnnotationsScanner())
    )
    return reflect.getMethodsAnnotatedWith(LifeInject::class.java).filter { method ->
        Modifier.isStatic(method.modifiers)
    }
}

object LifecycleLoader {

    private val taggedPool: TaggedConcurrentRunnablePool<LifeCycle> by lazy {
        TaggedConcurrentRunnablePool()
    }
    private val externalTaggedPool: TaggedConcurrentRunnablePool<LifeCycle> by lazy {
        TaggedConcurrentRunnablePool()
    }

    init {
        loadInnerLifecycle()
    }

    private fun loadInnerLifecycle() {
        val methods = scanInnerPackageLifeCycle()
        methods?.forEach { method ->
            val annotation = method.getAnnotation(LifeInject::class.java)
            annotation.type.iterator().forEach { type ->
                taggedPool.takeIf { Modifier.isStatic(method.modifiers) }?.push(type, annotation.priority) {
                    method.trySetAccessible()
                    method.invoke(null)
                }
            }
        }
    }

    @LifeInject([LifeCycle.CakeReload])
    @JvmStatic
    private fun onReload() {
        externalTaggedPool.clear()
    }

    internal fun addExternalLifecycle(pkgName: String, vararg loader: ClassLoader) {
        val methods = scanPackageLifecycle(pkgName, *loader)
        methods.forEach{ method ->
            assert(Modifier.isStatic(method.modifiers))
            val annotation = method.getAnnotation(LifeInject::class.java)
            annotation.type.iterator().forEach { type ->
                externalTaggedPool.push(type, annotation.priority) {
                    method.trySetAccessible()
                    method.invoke(null)
                }
            }
        }
    }

    internal fun runLifecycle(cycle: LifeCycle) = taggedPool.runTag(cycle).also { externalTaggedPool.runTag(cycle) }

}
