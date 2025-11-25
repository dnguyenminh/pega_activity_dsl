package com.pega.dsl

import spock.lang.Specification

class ButtonElementTest extends Specification {

    def "should create a button element"() {
        when:
        def button = new ButtonElement()

        then:
        button.type == 'Button'
        button.style == 'Standard'
    }

    def "should set visibility"() {
        given:
        def button = new ButtonElement()

        when:
        button.visible 'some_condition'

        then:
        button.visibility == 'If'
        button.condition == 'some_condition'
    }

    def "should set required property"() {
        given:
        def button = new ButtonElement()

        when:
        button.required()

        then:
        button.properties['required']
    }

    def "should set readOnly property"() {
        given:
        def button = new ButtonElement()

        when:
        button.readOnly()

        then:
        button.properties['readOnly']
    }

    def "should set disabled property"() {
        given:
        def button = new ButtonElement()

        when:
        button.disabled()

        then:
        button.properties['disabled']
    }

    def "should set primary style"() {
        given:
        def button = new ButtonElement()

        when:
        button.primary()

        then:
        button.style == 'Primary'
    }

    def "should set secondary style"() {
        given:
        def button = new ButtonElement()

        when:
        button.secondary()

        then:
        button.style == 'Secondary'
    }
    
    def "should set tertiary style"() {
        given:
        def button = new ButtonElement()

        when:
        button.tertiary()

        then:
        button.style == 'Tertiary'
    }
    
    def "should set simple style"() {
        given:
        def button = new ButtonElement()

        when:
        button.simple()

        then:
        button.style == 'Simple'
    }

    def "should set strong style"() {
        given:
        def button = new ButtonElement()

        when:
        button.strong()

        then:
        button.style == 'Strong'
    }

    def "should set local action"() {
        given:
        def button = new ButtonElement()

        when:
        button.localAction 'MyLocalAction'

        then:
        button.action == 'MyLocalAction'
    }

    def "should set flow action"() {
        given:
        def button = new ButtonElement()

        when:
        button.flowAction 'MyFlowAction'

        then:
        button.action == 'MyFlowAction'
    }

    def "should set activity action"() {
        given:
        def button = new ButtonElement()

        when:
        button.activity 'MyActivity'

        then:
        button.action == 'MyActivity'
    }

    def "should set the label"() {
        given:
        def button = new ButtonElement()

        when:
        button.label = 'Click Me'

        then:
        button.label == 'Click Me'
    }
}