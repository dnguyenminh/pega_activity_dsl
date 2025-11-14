package com.pega.pegarules.pub.clipboard

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import java.time.Instant

class BaseClassDefaultsTest {
    @Test
    void baseClassInitializesTimestampsAndDefaults() {
        def page = new BaseClass()

        // pxCreateDateTime should be a ClipboardProperty containing an ISO instant
        def createDtProp = page.getProperty('pxCreateDateTime')
        assertNotNull(createDtProp, 'pxCreateDateTime property should exist')
        assertTrue(createDtProp instanceof ClipboardProperty)
        def createDtStr = createDtProp.getStringValue()
        assertNotNull(createDtStr)
        // parse to verify ISO_INSTANT format
        Instant.parse(createDtStr)

        // pxCreateDate should be a ClipboardProperty containing an ISO local date
        def createDateProp = page.getProperty('pxCreateDate')
        assertNotNull(createDateProp, 'pxCreateDate property should exist')
        def createDateStr = createDateProp.getStringValue()
        assertNotNull(createDateStr)
        // parse to LocalDate to verify format (will throw if invalid)
        java.time.LocalDate.parse(createDateStr)

        // pyLabel defaults to empty string and should be stored as a property
        def labelProp = page.getProperty('pyLabel')
        assertNotNull(labelProp)
        assertEquals('', labelProp.getStringValue())

        // pxObjClass default from BaseClass should be present
        def objClassProp = page.getProperty('pxObjClass')
        assertNotNull(objClassProp)
        assertEquals('@baseclass', objClassProp.getStringValue())

        // IMPORTANT: verify that every stored property on the page delegate is a ClipboardProperty
        // (the simulator requires page properties to be ClipboardProperty instances such as SingleValue)
        def del = page.@delegate
        assertNotNull(del)
        del.each { k, v ->
            assertTrue(v instanceof ClipboardProperty, "delegate entry ${k} should be a ClipboardProperty but was ${v?.getClass()?.name}")
        }
    }
}
