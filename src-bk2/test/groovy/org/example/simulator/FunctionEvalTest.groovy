package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import com.pega.pegarules.pub.clipboard.ClipboardPropertyType
import static org.example.simulator.ClipboardDsl.clipboard

class FunctionEvalTest {
    @Test
    void functionCallSyntaxLengthOfPageListIsEvaluated() {
        def cb = clipboard {}

        // Build activity that creates Orders and then iterates using @LengthOfPageList(Orders)
        def act = SimulatorRunner.activity('FunctionEval') {
            step('Page-New', [params: [page: 'Orders']])
            // create three entries
            step('Property-Set', [params: [page: 'Orders.pxResults(1)', value: [[amount:1, type: ClipboardPropertyType.INTEGER]] ]])
            step('Property-Set', [params: [page: 'Orders.pxResults(2)', value: [[amount:2, type: ClipboardPropertyType.INTEGER]] ]])
            step('Property-Set', [params: [page: 'Orders.pxResults(3)', value: [[amount:3, type: ClipboardPropertyType.INTEGER]] ]])

            // loop that uses the registered function via @LengthOfPageList(Orders)
            step('', [loop: [start: 0, end: '@LengthOfPageList(Orders)'] , label: 'counter']) {
                // on each iteration append a marker into Results.pxResults using pyForEachCount
                // Use a closure so Step.evalParam can build the target page string using ctx['pyForEachCount'] at runtime
                step('Property-Set', [params: [page: { cb2, ctx -> "Results.pxResults(${ctx['pyForEachCount']})" }, value: [[marker:true]] ]])
            }
        }

        def execCtx = [
            env      : [methods: SimulatorRunner.methodRegistry, datatransforms: SimulatorRunner.datatransforms, decisionTables: SimulatorRunner.decisionTables],
            className: 'org.example.simulator.FunctionEval',
            paramPage: [Orders: cb.get('Orders')]
        ]

        SimulatorRunner.runActivity(act, cb, execCtx)

        def results = cb.get('Results')
        def resultsList = (results instanceof com.pega.pegarules.pub.clipboard.ClipboardProperty) ? ((com.pega.pegarules.pub.clipboard.ClipboardProperty) results).getPropertyValue() : results
        // Expect three entries created by the loop
        assertNotNull(resultsList)
        assertEquals(3, resultsList.size())
    }
}
