package com.pega.dsl

import spock.lang.Specification

class TestCaseExtraSpec extends Specification {

    def "input and inputData update both maps"() {
        given:
        def tc = new TestCase()

        when:
        tc.input('k1', 123)
        tc.inputData('k2', 'v')

        then:
        tc.inputs['k1'] == 123
        tc.inputData['k1'] == 123
        tc.inputData['k2'] == 'v'
    }

    def "step with closure delegates to step map and is returned"() {
        given:
        def tc = new TestCase()

        when:
        def step = tc.step('Step1', { params['p'] = 'x' })

        then:
        step.name == 'Step1'
        step.params['p'] == 'x'
        tc.steps.size() == 1
    }

    def "expect and assertion helpers record assertions and behave for string and boolean forms"() {
        given:
        def tc = new TestCase()

        when:
        tc.expect('out', 'ok')
        def t1 = tc.assertTrue('a==a')
        def t2 = tc.assertTrue(true)
        def eq = tc.assertEquals(1, 1)
        def nn = tc.assertNotNull(null)

        then:
        tc.expectedResults['out'] == 'ok'
        t1 == true
        t2 == true
        eq == true
        nn == false
        // assertions were recorded
        tc.assertions.find { it.type == 'assertTrue' }
        tc.assertions.find { it.type == 'assertEquals' }
        tc.assertions.find { it.type == 'assertNotNull' }
    }
}
