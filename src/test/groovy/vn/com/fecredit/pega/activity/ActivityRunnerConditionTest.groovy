package org.example

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class ActivityRunnerConditionTest {

    @Test
    void "evaluateCondition uses WhenRegistry by name"() {
        org.example.WhenRegistry.clear()
        org.example.WhenRegistry.register('IsFoo', { ctx -> ctx['foo'] == 'bar' })
        def ctx = [foo: 'bar']
        assertTrue(org.example.ActivityRunner.evaluateCondition('IsFoo', ctx))
        assertTrue(org.example.ActivityRunner.evaluateCondition('When:IsFoo', ctx))
    }

    @Test
    void "evaluateCondition handles groovy-like expressions via ExpressionEvaluator"() {
        def ctx = [count: 3, foo: 'bar']
        assertTrue(org.example.ActivityRunner.evaluateCondition("count >= 3 && foo == 'bar'", ctx))
        assertFalse(org.example.ActivityRunner.evaluateCondition("count < 2 || foo == 'baz'", ctx))
    }

    @Test
    void "evaluateCondition fallback equality and property truthiness"() {
        def ctx = [a: 'x', b: null]
        assertTrue(org.example.ActivityRunner.evaluateCondition("a == 'x'", ctx))
        assertFalse(org.example.ActivityRunner.evaluateCondition("b", ctx))
    }

    @Test
    void "precondition and whenCondition evaluation on step objects via ActivityRunner logic"() {
        def s = new org.example.Step()
        s.precondition = { ctx -> ctx['ok'] == true }
        s.whenCondition = { ctx -> ctx['flag'] == 'y' }
        def ctx = [ok: true, flag: 'y']
        assertTrue(org.example.ActivityRunner.evaluateCondition(s.precondition, ctx))
        assertTrue(org.example.ActivityRunner.evaluateCondition(s.whenCondition, ctx))
    }
}

