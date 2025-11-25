package com.pega.dsl

import spock.lang.Specification

class EndShapeTest extends Specification {

    def "should create an end shape with default status"() {
        when:
        def shape = new EndShape()

        then:
        shape.type == 'End'
        shape.status == 'Resolved-Completed'
    }

    def "should set status to resolved"() {
        given:
        def shape = new EndShape()
        shape.status 'SomethingElse' // Start with a different status

        when:
        shape.resolved()

        then:
        shape.status == 'Resolved-Completed'
    }

    def "should set status to cancelled"() {
        given:
        def shape = new EndShape()

        when:
        shape.cancelled()

        then:
        shape.status == 'Resolved-Cancelled'
    }

    def "should set status to withdrawn"() {
        given:
        def shape = new EndShape()

        when:
        shape.withdrawn()

        then:
        shape.status == 'Resolved-Withdrawn'
    }

    def "should set a custom status"() {
        given:
        def shape = new EndShape()

        when:
        shape.status 'MyCustomStatus'

        then:
        shape.status == 'MyCustomStatus'
    }
}
