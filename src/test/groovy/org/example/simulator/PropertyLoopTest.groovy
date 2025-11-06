package org.example.simulator

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class PropertyLoopTest {
    @Test
    void propertylist_loop_calls_collect_with_each_value() {
        def cb = new Clipboard()
        cb.set('Numbers', [10,20,30])

        // prepare collector in env so handlers can append to it
        def env = [collected: []]

        SimulatorRunner.registerMethod('Collect', { Clipboard c, Map p, Map ctx ->
            ctx['__env']['collected'] << ctx['pyPropertyValue']
            // also mirror into paramPage for test purposes if present
            if(ctx['__paramPage'] instanceof Map) ctx['__paramPage']['lastCollected'] = ctx['pyPropertyValue']
        })

        def act = SimulatorRunner.activity('PropList') {
            step('', [loop:[property:'Numbers', indexVar:'i']]) {
                step('Collect', [params:[:]])
            }
        }

        SimulatorRunner.runActivity(act, cb, [env:env, className:'t'])
        assertEquals([10,20,30], env.collected)
    }

    @Test
    void propertygroup_loop_exposes_key_and_value() {
        def cb = new Clipboard()
        // insertion-order map to make result deterministic
        def map = [:]
        map['a'] = 1; map['b'] = 2; map['c'] = 3
        cb.set('Sets', map)

        def env = [collected: []]
        SimulatorRunner.registerMethod('CollectKV', { Clipboard c, Map p, Map ctx ->
            ctx['__env']['collected'] << [ctx['pyPropertyReference'], ctx['pyPropertyValue']]
            if(ctx['__paramPage'] instanceof Map) ctx['__paramPage']['lastKV'] = [ctx['pyPropertyReference'], ctx['pyPropertyValue']]
        })

        def act = SimulatorRunner.activity('PropGroup') {
            step('', [loop:[property:'Sets', indexVar:'i']]) {
                step('CollectKV', [params:[:]])
            }
        }

        SimulatorRunner.runActivity(act, cb, [env:env, className:'t'])
        assertEquals([['a',1],['b',2],['c',3]], env.collected)
    }

    @Test
    void empty_collections_do_not_error_and_collect_nothing() {
        def cb = new Clipboard()
        cb.set('Numbers', [])
        cb.set('Sets', [:])
        def env = [collected: []]
        SimulatorRunner.registerMethod('Noop', { Clipboard c, Map p, Map ctx -> ctx['__env']['collected'] << 'x' })
        def a1 = SimulatorRunner.activity('EmptyList') { step('', [loop:[property:'Numbers']]) { step('Noop', [params:[:] ]) } }
        def a2 = SimulatorRunner.activity('EmptyMap') { step('', [loop:[property:'Sets']]) { step('Noop', [params:[:] ]) } }
        SimulatorRunner.runActivity(a1, cb, [env:env, className:'t'])
        SimulatorRunner.runActivity(a2, cb, [env:env, className:'t'])
        assertEquals([], env.collected)
    }

    @Test
    void nested_pages_and_paramPage_mirroring() {
        def cb = new Clipboard()
        cb.set('Parent.Children', [7,8,9])
        def paramPage = [:]
        def env = [collected: []]
        SimulatorRunner.registerMethod('Record', { Clipboard c, Map p, Map ctx ->
            env.collected << ctx['pyPropertyValue']
            // record last pyForEachCount into env so test can observe mirroring
            ctx['__env']['lastPy'] = ctx['pyForEachCount']
        })
        def act = SimulatorRunner.activity('Nested') { step('', [loop:[property:'Parent.Children']]) { step('Record', [params:[:]]) } }
        SimulatorRunner.runActivity(act, cb, [env:env, className:'t', paramPage:paramPage])
        assertEquals([7,8,9], env.collected)
        // recorded into env by the handler
        assertEquals(3, env.lastPy)
    }
}
