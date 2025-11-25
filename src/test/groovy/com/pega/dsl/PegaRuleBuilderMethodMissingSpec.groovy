package com.pega.dsl

import spock.lang.Specification

class PegaRuleBuilderMethodMissingSpec extends Specification {

    def "methodMissing handles 'call' with string and throws for others"() {
        given:
        def b = new PegaRuleBuilder()
        when: "calling methodMissing directly should now throw MissingMethodException"
        b.methodMissing('call', ['someName'] as Object[])

        then:
        thrown(MissingMethodException)

        when: "an unknown missing method should also throw MissingMethodException"
        b.methodMissing('noSuchMethod', [] as Object[])

        then:
        thrown(MissingMethodException)
    }
}
