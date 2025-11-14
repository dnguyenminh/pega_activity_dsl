package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import com.pega.pegarules.pub.clipboard.ClipboardPropertyType
import static org.example.simulator.ClipboardDsl.pageList
import static org.example.simulator.ClipboardDsl.clipboard

class ActivityBubbleSortTest {
    @Test
    void bubbleSortPageListUsingActivitySteps() {
        // setup empty clipboard; the activity will create the 'Orders' page-list via steps
        def cb = clipboard {}

        // default handlers are registered in the engine (SimulatorRunner) so the test can be pure DSL

        // Build activity using the DSL helpers (nested step(...) closures)
        def act = SimulatorRunner.activity('SortOrders') {
            // create the Orders page-list inside the activity (use page ops: Page-New + Page-Add per entry)
            step('Page-New', [params: [page: 'Orders']])
            step('Property-Set', [params: [page: 'Orders.pxResults(1)', value: [[amount:5, type: ClipboardPropertyType.INTEGER], [id:'a', type: ClipboardPropertyType.STRING] ] ]])
            step('Property-Set', [params: [page: 'Orders.pxResults(2)', value: [[amount:2, type: ClipboardPropertyType.INTEGER], [id:'b', type: ClipboardPropertyType.STRING] ] ]])
            step('Property-Set', [params: [page: 'Orders.pxResults(3)', value: [[amount:9, type: ClipboardPropertyType.INTEGER], [id:'c', type: ClipboardPropertyType.STRING] ] ]])
            step('Property-Set', [params: [page: 'Orders.pxResults(4)', value: [[amount:1, type: ClipboardPropertyType.INTEGER], [id:'d', type: ClipboardPropertyType.STRING] ] ]])

            // outer loop: for i in 0..size-1 (Loop is an option on a step)
            // use Pega-style function expression for the loop end
            step('', [loop: [start: 0, end: '@LengthOfPageList(Orders)'], label: 'outer']) {
                step('Property-Set', [params: [page: 'local.loop2', value: { cb2, ctx ->
                    def size = SimulatorRunner.lengthOfPageList(cb2, 'Orders')
                    def count = (ctx['pyForEachCount'] ?: 1) as int
                    return ((size - count) > 1) ? (size - count) : 1
                } ]])
                // inner loop: for j in 0..(size-2-i) (inner loop is also an option)
                // use pyForEachCount (1-based) instead of relying on ctx['i'] being present                
                step('', [loop: [start: 0, end: 'local.loop2'], label: 'inner']) {
                    // conditional compare attached to the actual swap step via when option
                    // reference a named When rule for clarity (no inline Groovy in the step)
                    step('Property-Set', [when: 'AmountGreaterThanNext', params: [action: 'swap', page: 'Orders']])
                }
            }
        }
        // Run activity with execution context (environment, class, parameter page)
        def execCtx = [
                env      : [methods: SimulatorRunner.methodRegistry, datatransforms: SimulatorRunner.datatransforms, decisionTables: SimulatorRunner.decisionTables],
                className: 'org.example.simulator.SortOrders',
                paramPage: [Orders: cb.get('Orders')]
        ]
        SimulatorRunner.runActivity(act, cb, execCtx)
        def sorted = cb.get('Orders')
        def sortedList = (sorted instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty) sorted).getPropertyValue() : sorted
        println "DEBUG sorted amounts: ${sortedList.collect { [it['amount'], it['amount']?.getClass()?.name] }}"
        sortedList.eachWithIndex { p, i ->
            try {
                println "PAGE[${i}] delegate: ${p.@delegate}"
                p.@delegate.each { k, v -> println "  prop ${k} -> ${v} (value: ${v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty ? v.getPropertyValue() : 'N/A'})" }
            } catch (e) {
                println "  cannot inspect delegate: $e"
            }
        }
        def actualList = sortedList.collect {
                try {
                def raw = (it instanceof com.pega.pegarules.pub.clipboard.AbstractClipboardPage) ? it.@delegate.get('amount') : (it instanceof Map ? it['amount'] : it)
                if (raw instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) return raw.getPropertyValue()
                return raw
            } catch (Exception e) {
                return null
            }
        }
        println "DEBUG actual element types: ${actualList.collect { it?.getClass()?.name }}"
        def normalized = actualList.collect { v ->
            def val = v
            try {
                // duck-type unwrap: repeatedly call getPropertyValue() while available
                def m = val?.metaClass?.getMetaMethod('getPropertyValue')
                while(m) { val = val.getPropertyValue(); m = val?.metaClass?.getMetaMethod('getPropertyValue') }
            } catch(Exception ignore) { }
            return val as Integer
        }
        println "DEBUG normalized: ${normalized}"
        assertEquals([1, 2, 5, 9], normalized)
    }
}