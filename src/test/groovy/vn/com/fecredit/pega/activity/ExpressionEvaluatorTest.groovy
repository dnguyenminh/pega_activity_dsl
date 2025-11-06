package org.example

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class ExpressionEvaluatorTest {

    @Test
    void "numeric comparison and logical AND"() {
        def ctx = [count:5, foo:'bar']
        assertTrue(ExpressionEvaluator.evaluate("count > 1 && foo == 'bar'", ctx))
    }

    @Test
    void "dotted property access"() {
        def ctx = [page:[one:[value:123]]]
        assertTrue(ExpressionEvaluator.evaluate("page.one.value == 123", ctx))
    }

    @Test
    void "not and or operators"() {
        assertTrue(ExpressionEvaluator.evaluate("!false", [:]))
        assertTrue(ExpressionEvaluator.evaluate("false || true", [:]))
    }

    @Test
    void "zero is falsy"() {
        assertFalse(ExpressionEvaluator.evaluate("0", [:]))
    }

    @Test
    void "string equality"() {
        def ctx = [name:'alice']
        assertTrue(ExpressionEvaluator.evaluate("name == 'alice'", ctx))
        assertFalse(ExpressionEvaluator.evaluate("name == 'bob'", ctx))
    }
}

