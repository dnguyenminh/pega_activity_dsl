package com.pega.dsl

import spock.lang.Specification

class FlowBuilderMethodMissingSpec extends Specification {

    def "methodMissing returns this for call with String arg when invoked directly"() {
        given:
        def flow = new Flow()
        def b = new FlowBuilder(flow)

        when:
        // call methodMissing directly to exercise the branch guarded for 'call'
        def result = b.methodMissing('call', ['someName'] as Object[])

        then:
        result.is(b)
    }

    def "methodMissing throws MissingMethodException for unknown method names"() {
        given:
        def flow = new Flow()
        def b = new FlowBuilder(flow)

        when:
        b.methodMissing('iDontExist', [] as Object[])

        then:
        thrown(MissingMethodException)
    }
}
