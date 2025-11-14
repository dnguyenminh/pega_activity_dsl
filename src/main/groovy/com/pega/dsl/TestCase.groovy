package com.pega.dsl

class TestCase extends Rule {
    Map<String, Object> inputs = [:]
    Map<String, Object> inputData = [:]
    Map<String, Object> expectedResults = [:]
    List<Map<String, Object>> steps = []
    List<Map<String, Object>> assertions = []
    String status = 'Ready'
    String description
    String ruleToTest

    TestCase() {
        this.type = 'TestCase'
    }

    // Accept input into both legacy maps used by tests
    def input(String name, Object value) {
        inputs[name] = value
        inputData[name] = value
        return this
    }

    def inputData(String name, Object value) {
        inputData[name] = value
        return this
    }

    def expectedResult(String name, Object value) {
        expectedResults[name] = value
        return this
    }

    def step(String name, Closure closure) {
        def step = [name: name, params: [:]]
        if (closure) {
            closure.delegate = step
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
        steps.add(step)
        return step
    }

    def description(String description) {
        this.description = description
        return this
    }

    def setStatus(String status) {
        this.status = status
    }

    def status(String status) {
        this.status = status
    }

    // Minimal test APIs referenced by unit tests
    def ruleToTest(String ruleName) {
        this.ruleToTest = ruleName
        properties['ruleToTest'] = ruleName
        return this
    }

    def getRuleToTest() {
        return this.ruleToTest ?: properties['ruleToTest']
    }

    def expect(String name, Object value = null) {
        expectedResults[name] = value
        return this
    }

    // Assertion helpers: store assertions with a 'type' key to match tests
    def assertTrue(Object exprOrBool) {
        if (exprOrBool instanceof String) {
            assertions.add([type: 'assertTrue', expr: exprOrBool])
            return true
        } else {
            boolean cond = (exprOrBool as boolean)
            assertions.add([type: 'assertTrue', result: cond])
            return cond
        }
    }

    def assertEquals(Object a, Object b) {
        def ok = (a == b)
        assertions.add([type: 'assertEquals', expected: a, actual: b, result: ok])
        return ok
    }

    def assertNotNull(Object obj) {
        if (obj instanceof String) {
            assertions.add([type: 'assertNotNull', expr: obj])
            return true
        } else {
            def ok = (obj != null)
            assertions.add([type: 'assertNotNull', obj: obj, result: ok])
            return ok
        }
    }
}
