package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import static org.example.simulator.ClipboardDsl.clipboard
import com.pega.pegarules.pub.clipboard.ClipboardPropertyType

class PropertySetIndexedTest {
    @Test
    void propertySetIndexedCreatesPageInPxResults() {
        def cb = clipboard {}

        // invoke the registered Property-Set handler directly
        def handler = SimulatorRunner.methodRegistry['Property-Set']
        assertNotNull(handler)

        // create Orders page
        SimulatorRunner.methodRegistry['Page-New'](cb, [page: 'Orders'], [:])

        // set indexed entry using the test-style value shape (list of prop maps)
        handler(cb, [page: 'Orders.pxResults(1)', value: [[amount:10, type: ClipboardPropertyType.INTEGER], [id:'x', type: ClipboardPropertyType.STRING]]], [:])

        // retrieve Orders via cb.get (should return a ClipboardProperty for pxResults)
        def ordersProp = cb.get('Orders')
        assertNotNull(ordersProp)
        def ordersList = (ordersProp instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty)ordersProp).getPropertyValue() : ordersProp
        assertTrue(ordersList instanceof List)
        assertEquals(1, ordersList.size())

        def first = ordersList[0]

        // helper: try multiple access patterns to obtain a ClipboardProperty for a named field
        def findProp = { elem, fieldName ->
                if(elem instanceof com.pega.pegarules.pub.clipboard.ClipboardPage) {
                    // ask the page for the ClipboardProperty directly (getProperty returns a ClipboardProperty)
                    def p = elem.getProperty(fieldName)
                    // only check for presence (null-check). Do not assert the concrete implementation here.
                    if(p != null) return p
                    throw new IllegalStateException("missing property ${fieldName}")
                }
        }

        def amountProp = findProp(first, 'amount')
        assertNotNull(amountProp, 'amount property should be present and wrapped')
        assertTrue(amountProp instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty)
        assertEquals(10, ((com.pega.pegarules.pub.clipboard.ClipboardProperty)amountProp).getPropertyValue())

        def idProp = findProp(first, 'id')
        assertNotNull(idProp, 'id property should be present and wrapped')
        assertTrue(idProp instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty)
        assertEquals('x', ((com.pega.pegarules.pub.clipboard.ClipboardProperty)idProp).getPropertyValue())
    }
}
