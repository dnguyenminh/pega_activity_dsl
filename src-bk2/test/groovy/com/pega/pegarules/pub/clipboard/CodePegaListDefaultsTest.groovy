package com.pega.pegarules.pub.clipboard

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import java.time.Instant

class CodePegaListDefaultsTest {
    @Test
    void codePegaListInitializesPxResultsAndTimestamps() {
        def list = new CodePegaList()

        // pxObjClass should be the marker for list pages
        assertEquals('Code-Pega-List', list.get('pxObjClass'))

        // pxResults should be a ClipboardProperty whose value is a List
        def pxResultsProp = list.getProperty('pxResults')
        assertNotNull(pxResultsProp, 'pxResults property should exist')
        assertTrue(pxResultsProp instanceof ClipboardProperty)
        def resultsVal = pxResultsProp.getPropertyValue()
        assertNotNull(resultsVal)
        assertTrue(resultsVal instanceof List)

        // Ensure timestamps are present and parseable
        def createDtProp = list.getProperty('pxCreateDateTime')
        assertNotNull(createDtProp)
        Instant.parse(createDtProp.getStringValue())

        def updateDtProp = list.getProperty('pxUpdateDateTime')
        assertNotNull(updateDtProp)
        Instant.parse(updateDtProp.getStringValue())

        // Verify all properties stored on the page are ClipboardProperty instances
        def del = list.@delegate
        assertNotNull(del)
        del.each { k, v ->
            assertTrue(v instanceof ClipboardProperty, "delegate entry ${k} should be a ClipboardProperty but was ${v?.getClass()?.name}")
        }
    }
}
