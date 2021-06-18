package pro.darc.cake.core.inject

import org.jetbrains.annotations.TestOnly
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
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

@LifeInject([LifeCycle.CakeLoad])
@TestOnly fun testInject() {}

fun scanPackageLifeCycle(): MutableSet<Method>? {
    val pkgName = CakeAPI::class.java.packageName
    val reflect = Reflections(
        ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(pkgName))
            .setScanners(MethodAnnotationsScanner())
    )
    return reflect.getMethodsAnnotatedWith(LifeInject::class.java)
}

object LifecycleLoader {

    private val taggedPool: TaggedConcurrentRunnablePool<LifeCycle> by lazy {
        TaggedConcurrentRunnablePool()
    }

    init {
        loadInnerLifecycle()
    }

    private fun loadInnerLifecycle() {
        val methods = scanPackageLifeCycle()
        methods?.iterator()?.forEach { method ->
            val annotation = method.getAnnotation(LifeInject::class.java)
            annotation.type.iterator().forEach { type ->
                taggedPool.takeIf { Modifier.isStatic(method.modifiers) }?.push(type, annotation.priority) {
                    method.invoke(null)
                }
            }
        }
    }

    internal fun runLifecycle(cycle: LifeCycle) = taggedPool.runTag(cycle)

}
