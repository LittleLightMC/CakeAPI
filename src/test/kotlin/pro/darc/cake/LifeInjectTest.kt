package pro.darc.cake

import TestBase
import org.junit.jupiter.api.Test
import pro.darc.cake.core.inject.LifecycleLoader
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import kotlin.test.assertTrue


internal class LifeInjectTest: TestBase() {


    companion object {
        private val loader = LifecycleLoader

        private var flag1 = false

        @JvmStatic
        @LifeInject([LifeCycle.CakeTest])
        fun for_test_inject() {
            flag1 = true
        }
    }

    @Test
    fun `test load inner life cycle injector`() {
        loader.runLifecycle(LifeCycle.CakeTest)
        assertTrue("Failed to execute test inject function") { flag1 }
    }

}