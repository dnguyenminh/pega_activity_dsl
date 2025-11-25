package com.pega.dsl

import spock.lang.Specification

class StartShapeTest extends Specification {

    def "should create a start shape"() {
        when:
        def shape = new StartShape()

        then:
        shape.type == 'Start'
    }
}
