package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class ActivityBubbleSortTest {
    @Test
    void bubbleSortPageListUsingActivitySteps() {
        // setup clipboard with unsorted page list
        def cb = new Clipboard()
        def list = new com.pega.pegarules.pub.clipboard.PageList([
                new com.pega.pegarules.pub.clipboard.SimpleClipboardPage([
                        new com.pega.pegarules.pub.clipboard.NamedProperty(
                                "amount", 5, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.INTEGER
                        ),
                        new com.pega.pegarules.pub.clipboard.NamedProperty(
                                "id", "a", com.pega.pegarules.pub.clipboard.ClipboardPropertyType.STRING
                        )
                ]),
                new com.pega.pegarules.pub.clipboard.SimpleClipboardPage([
                        new com.pega.pegarules.pub.clipboard.NamedProperty("amount", 2, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.INTEGER),
                        new com.pega.pegarules.pub.clipboard.NamedProperty("id", "b", com.pega.pegarules.pub.clipboard.ClipboardPropertyType.STRING)
                ]),
                new com.pega.pegarules.pub.clipboard.SimpleClipboardPage([
                        new com.pega.pegarules.pub.clipboard.NamedProperty("amount", 9, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.INTEGER),
                        new com.pega.pegarules.pub.clipboard.NamedProperty("id", "c", com.pega.pegarules.pub.clipboard.ClipboardPropertyType.STRING)
                ]),
                new com.pega.pegarules.pub.clipboard.SimpleClipboardPage([
                        new com.pega.pegarules.pub.clipboard.NamedProperty("amount", 1, com.pega.pegarules.pub.clipboard.ClipboardPropertyType.INTEGER),
                        new com.pega.pegarules.pub.clipboard.NamedProperty("id", "d", com.pega.pegarules.pub.clipboard.ClipboardPropertyType.STRING)
                ])
        ])
        cb.set('Orders', list)
        // debug: inspect pages immediately after set (unwrap PageList wrapper if present)
        def initial = cb.get('Orders')
        def initialList = (initial instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty) initial).getPropertyValue() : initial
        println "DEBUG initial amounts: ${initialList.collect { [it['amount'], it['amount']?.getClass()?.name] }}"

        // default handlers are registered in the engine (SimulatorRunner) so the test can be pure DSL

        // Build activity using the DSL helpers (nested step(...) closures)
        def act = SimulatorRunner.activity('SortOrders') {
            // pre-step to compute size
            step('Property-Set', [params: [action: 'computeSize', page: 'Orders']])

            // outer loop: for i in 0..size-1 (Loop is an option on a step)
            step('', [loop: [start: 0, end: { cb2, ctx -> (ctx['__size'] ?: 0) }, indexVar: 'i'], label: 'outer']) {
                // inner loop: for j in 0..(size-2-i) (inner loop is also an option)
                step('', [loop: [start: 0, end: { cb2, ctx -> (ctx['__size'] ?: 0) - 1 - ctx['i'] }, indexVar: 'j'], label: 'inner']) {
                    // conditional compare attached to the actual swap step via when option
                    // reference a named When rule for clarity (no inline Groovy in the step)
                    step('Property-Set', [when: 'AmountGreaterThanNext', params: [action: 'swap', page: 'Orders', indexVar: 'j']])
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
                def raw = (it instanceof com.pega.pegarules.pub.clipboard.SimpleClipboardPage) ? it.@delegate.get('amount') : (it instanceof Map ? it['amount'] : it)
                if (raw instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) return raw.getPropertyValue()
                return raw
            } catch (Exception e) {
                return null
            }
        }
        println "DEBUG actual element types: ${actualList.collect { it?.getClass()?.name }}"
        def normalized = actualList.collect { v ->
            if (v instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) return ((com.pega.pegarules.pub.clipboard.ClipboardProperty) v).getPropertyValue() as Integer
            return v as Integer
        }
        println "DEBUG normalized: ${normalized}"
        assertEquals([1, 2, 5, 9], normalized)
    }
}