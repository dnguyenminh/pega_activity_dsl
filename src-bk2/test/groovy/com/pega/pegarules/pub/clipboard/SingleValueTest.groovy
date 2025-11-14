package com.pega.pegarules.pub.clipboard

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class SingleValueTest {
    @Test
    void singleValueThreeArgConstructor() {
        def sv = new SingleValue("myProp", "hello", ClipboardPropertyType.STRING)
        assertNotNull(sv)
        assertTrue(sv instanceof ClipboardProperty)
        assertEquals('myProp', sv.getName())
        assertEquals('hello', sv.getStringValue())
    // API returns integer type codes (see ClipboardProperty.TYPE_*)
    assertEquals(ClipboardProperty.TYPE_TEXT, sv.getType())
    }

    @Test
    void factoryCreatesStringDefault() {
        def p = ClipboardFactory.newProperty('pxFoo', 'bar')
        assertNotNull(p)
        assertTrue(p instanceof ClipboardProperty)
        assertEquals('bar', p.getStringValue())
    assertEquals(ClipboardProperty.TYPE_TEXT, p.getType())
    }
}
