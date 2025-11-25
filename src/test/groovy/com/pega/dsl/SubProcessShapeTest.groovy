package com.pega.dsl

import spock.lang.Specification

class SubProcessShapeTest extends Specification {

    def "should create a sub-process shape"() {
        when:
        def shape = new SubProcessShape()

        then:
        shape.type == 'SubProcess'
    }

    def "should set the sub-flow"() {
        given:
        def shape = new SubProcessShape()

        when:
        shape.subFlow = 'MySubFlow'

        then:
        shape.subFlow == 'MySubFlow'
    }
}
