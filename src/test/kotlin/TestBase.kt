import org.junit.jupiter.api.BeforeAll
import org.mockito.Mockito
import pro.darc.cake.CakeAPI

open class TestBase {

    companion object {
        @BeforeAll
        @JvmStatic
        fun init() {
            println("Mocking CakeAPI class...\t")
            CakeAPI.setInstance(Mockito.mock(CakeAPI::class.java))
        }
    }

}