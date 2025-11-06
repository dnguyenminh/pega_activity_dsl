package vn.com.fecredit.pega.activity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class WhenRegistryTest {

    @Test
    void "register and retrieve when rule"() {
        WhenRegistry.clear()
        WhenRegistry.register('TestRule', { ctx -> ctx['x'] == 10 })
        Assertions.assertTrue(WhenRegistry.contains('TestRule'))
        def c = WhenRegistry.get('TestRule')
        assertNotNull(c)
        assertTrue(c.call([x:10]))
        assertFalse(c.call([x:5]))
    }

    @Test
    void "register overwrite"() {
        WhenRegistry.clear()
        WhenRegistry.register('R', { ctx -> false })
        WhenRegistry.register('R', { ctx -> true })
        assertTrue(WhenRegistry.get('R').call([:]))
    }
}