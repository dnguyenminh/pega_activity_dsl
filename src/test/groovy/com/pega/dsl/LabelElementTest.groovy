package com.pega.dsl

import spock.lang.Specification

class LabelElementTest extends Specification {

    def "should create a label element"() {
        when:
        def element = new LabelElement()

        then:
        element.type == 'Label'
    }

    def "should set text and property"() {
        given:
        def element = new LabelElement()

        when:
        element.text = 'MyLabel'
        element.property = 'MyProperty'

        then:
        element.text == 'MyLabel'
        element.property == 'MyProperty'
    }
}
