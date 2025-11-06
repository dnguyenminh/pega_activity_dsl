package org.example

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class WhenRegistryTest {

    @Test
    void "register and retrieve when rule"() {
        org.example.WhenRegistry.clear()
        org.example.WhenRegistry.register('TestRule', { ctx -> ctx['x'] == 10 })
        assertTrue(org.example.WhenRegistry.contains('TestRule'))
        def c = org.example.WhenRegistry.get('TestRule')
        assertNotNull(c)
        assertTrue(c.call([x:10]))
        assertFalse(c.call([x:5]))
    }

    @Test
    void "register overwrite"() {
        org.example.WhenRegistry.clear()
        org.example.WhenRegistry.register('R', { ctx -> false })
        org.example.WhenRegistry.register('R', { ctx -> true })
        assertTrue(org.example.WhenRegistry.get('R').call([:]))
    }
}

