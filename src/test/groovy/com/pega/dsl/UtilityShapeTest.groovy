package com.pega.dsl

import spock.lang.Specification

class UtilityShapeTest extends Specification {

    def "should create a utility shape"() {
        when:
        def shape = new UtilityShape()

        then:
        shape.type == 'Utility'
    }

    def "should set the activity"() {
        given:
        def shape = new UtilityShape()

        when:
        shape.activity = 'MyUtilityActivity'

        then:
        shape.activity == 'MyUtilityActivity'
    }
}
