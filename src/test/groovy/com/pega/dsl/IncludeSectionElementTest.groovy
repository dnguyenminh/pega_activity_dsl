package com.pega.dsl

import spock.lang.Specification

class IncludeSectionElementTest extends Specification {

    def "should create an include section element"() {
        when:
        def element = new IncludeSectionElement()

        then:
        element.type == 'Include Section'
    }

    def "should set the section name"() {
        given:
        def element = new IncludeSectionElement()

        when:
        element.sectionName = 'MySection'

        then:
        element.sectionName == 'MySection'
    }

    def "should add a parameter"() {
        given:
        def element = new IncludeSectionElement()

        when:
        element.parameter('name', 'value')

        then:
        element.parameters.size() == 1
        element.parameters['name'] == 'value'
    }

    def "should set a when condition"() {
        given:
        def element = new IncludeSectionElement()

        when:
        def result = element.when('some_condition')

        then:
        element.condition == 'some_condition'
        result == element
    }
}
