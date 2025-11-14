package com.pega.dsl

import spock.lang.Specification
import static com.pega.dsl.PegaDeveloperUtilitiesDsl.*

class TestCaseSpec extends Specification {

    def "should accept inputs and inputData aliases"() {
        given:
        def tc = new TestCase()

        when:
        tc.input('id', 123)
        tc.inputData('name', 'Alice')

        then:
        tc.inputs['id'] == 123
        tc.inputData['name'] == 'Alice'
    }

    def "should record expected results and expect() alias"() {
        given:
        def tc = new TestCase()

        when:
        tc.expectedResult('Status', 'OK')
        tc.expect('Count', 5)

        then:
        tc.expectedResults['Status'] == 'OK'
        tc.expectedResults['Count'] == 5
    }

    def "step closure should populate params and return step map"() {
        given:
        def tc = new TestCase()

        when:
        def s = tc.step('DoThing') {
            params.action = 'run'
            params.timeout = 30
        }

        then:
        s.name == 'DoThing'
        s.params['action'] == 'run'
        s.params['timeout'] == 30
        tc.steps.size() == 1
    }

    def "ruleToTest getter and property storage"() {
        given:
        def tc = new TestCase()

        when:
        tc.ruleToTest('MyRule')

        then:
        tc.getRuleToTest() == 'MyRule'
        tc.properties['ruleToTest'] == 'MyRule'
    }

    def "assertion helpers should record and return correct results"() {
        given:
        def tc = new TestCase()

        expect:
        tc.assertTrue('some.expression')
        tc.assertNotNull('pyID')

        when:
        def eq = tc.assertEquals(2, 1+1)
        def neq = tc.assertEquals('a','b')

        then:
        eq
        !neq
        tc.assertions.any { it.type == 'assertTrue' }
        tc.assertions.any { it.type == 'assertEquals' }
        tc.assertions.any { it.type == 'assertNotNull' }
    }
}

