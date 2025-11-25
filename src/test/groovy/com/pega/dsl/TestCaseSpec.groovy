package com.pega.dsl

import spock.lang.Specification

class TestCaseSpec extends Specification {

    def "inputs and inputData are populated and step closure delegates"() {
        given:
        def tc = new TestCase()

        when:
        tc.input('a', 1)
        tc.inputData('b', 2)
        tc.expectedResult('e', 'v')

        def step = tc.step('step1') {
            params.foo = 'bar'
        }

        then:
        tc.inputs['a'] == 1
        tc.inputData['b'] == 2
        tc.expectedResults['e'] == 'v'
        step.name == 'step1'
        step.params.foo == 'bar'
    }

    def "status/description and ruleToTest APIs and expect/assert helpers"() {
        given:
        def tc = new TestCase()

        when:
        tc.description('desc')
        tc.setStatus('Running')
        tc.status('Paused')
        tc.ruleToTest('SomeRule')
        tc.expect('X', 5)

        then:
        tc.description == 'desc'
        tc.status == 'Paused'
        tc.getRuleToTest() == 'SomeRule'
        tc.expectedResults['X'] == 5

        when: "assert helpers"
        def t1 = tc.assertTrue('a==a')
        def t2 = tc.assertTrue(true)
        def eq = tc.assertEquals(1, 1)
        def nn = tc.assertNotNull('someExpr')

        then:
        t1 == true
        t2 == true
        eq == true
        nn == true
        tc.assertions.find { it.type == 'assertTrue' }
        tc.assertions.find { it.type == 'assertEquals' }
        tc.assertions.find { it.type == 'assertNotNull' }
    }
}

