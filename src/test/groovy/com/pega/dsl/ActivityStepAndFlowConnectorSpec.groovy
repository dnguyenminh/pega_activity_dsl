package com.pega.dsl

import spock.lang.Specification

class ActivityStepAndFlowConnectorSpec extends Specification {

    def "ActivityStep behaviors and accessors"() {
        given:
        def step = new ActivityStep()

        expect:
        step.doCall().is(step)

        when: "methodMissing with 'call' returns the step"
        def returned = step.methodMissing('call', ['1'] as Object[])

        then:
        returned.is(step)

        when: "methodMissing with unknown name throws"
        step.methodMissing('unknownMethod', [] as Object[])

        then:
        thrown(MissingMethodException)

        when: "parameters and convenience methods"
        step.parameter('Comment', 'a comment')
        step.parameter('PropertyName', 'page1')
        step.parameter('foo', 'bar')
        step.when('x == 1')
        step.transitionTo('2')
        step.iterate()

        then:
        step.comment == 'a comment'
        step.stepPage == 'page1'
        step.properties['foo'] == 'bar'
        step.condition == 'x == 1'
        step.transition == '2'
        step.isIterate
    }

    def "FlowConnector label and when"() {
        given:
        def c = new FlowConnector()

        when:
        c.label('myLabel')
        c.when('cond')

        then:
        c.label == 'myLabel'
        c.condition == 'cond'
    }
}
