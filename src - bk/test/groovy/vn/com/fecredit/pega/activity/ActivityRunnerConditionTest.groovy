package vn.com.fecredit.pega.activity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import vn.com.fecredit.pega.activity.model.Step

class ActivityRunnerConditionTest {

    @Test
    void "evaluateCondition uses WhenRegistry by name"() {
        WhenRegistry.clear()
        WhenRegistry.register('IsFoo', { ctx -> ctx['foo'] == 'bar' })
        def ctx = [foo: 'bar']
        Assertions.assertTrue(ActivityRunner.evaluateCondition('IsFoo', ctx))
        Assertions.assertTrue(ActivityRunner.evaluateCondition('When:IsFoo', ctx))
    }

    @Test
    void "evaluateCondition handles groovy-like expressions via ExpressionEvaluator"() {
        def ctx = [count: 3, foo: 'bar']
        Assertions.assertTrue(ActivityRunner.evaluateCondition("count >= 3 && foo == 'bar'", ctx))
        Assertions.assertFalse(ActivityRunner.evaluateCondition("count < 2 || foo == 'baz'", ctx))
    }

    @Test
    void "evaluateCondition fallback equality and property truthiness"() {
        def ctx = [a: 'x', b: null]
        Assertions.assertTrue(ActivityRunner.evaluateCondition("a == 'x'", ctx))
        Assertions.assertFalse(ActivityRunner.evaluateCondition("b", ctx))
    }

    @Test
    void "precondition and whenCondition evaluation on step objects via ActivityRunner logic"() {
        def s = new Step()
        s.precondition = { ctx -> ctx['ok'] == true }
        s.whenCondition = { ctx -> ctx['flag'] == 'y' }
        def ctx = [ok: true, flag: 'y']
        Assertions.assertTrue(ActivityRunner.evaluateCondition(s.precondition, ctx))
        Assertions.assertTrue(ActivityRunner.evaluateCondition(s.whenCondition, ctx))
    }
}