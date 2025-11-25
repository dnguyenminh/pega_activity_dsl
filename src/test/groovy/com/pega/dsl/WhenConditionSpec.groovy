package com.pega.dsl

import spock.lang.Specification

class WhenConditionSpec extends Specification {

    def "condition with closure and connectors are recorded"() {
        given:
        def w = new WhenCondition()

        when:
        def c1 = w.condition('Prop', '=', 'Val') {
            // delegate should be the clause
            connector = 'AND'
        }

        and:
        def c2 = w.and('EXISTS(.Attachments.DocumentID)')

        then:
        w.conditions.size() == 2
        c1.property == 'Prop'
        c1.operator == '='
        c1.value == 'Val'
        c1.connector == 'AND'

        c2.property == 'EXISTS(.Attachments.DocumentID)'
        c2.connector == 'AND'
    }

    def "or and explicit and(String) variants"() {
        given:
        def w = new WhenCondition()

        when:
        w.or('X', '==', 'Y')
        w.and('A', '!=', 'B')

        then:
        w.conditions.size() == 2
        w.conditions[0].connector == 'OR'
        w.conditions[1].connector == 'AND'
    }
}
