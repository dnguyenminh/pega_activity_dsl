package com.pega.dsl

import spock.lang.Specification

class InputElementTest extends Specification {

    def "should create an input element with default control"() {
        when:
        def element = new InputElement()

        then:
        element.type == 'Input'
        element.control == 'Text Input'
    }

    def "should set property and label"() {
        given:
        def element = new InputElement()

        when:
        element.property = 'MyProperty'
        element.label = 'MyLabel'

        then:
        element.property == 'MyProperty'
        element.label == 'MyLabel'
    }

    def "should set control to text area"() {
        given:
        def element = new InputElement()

        when:
        element.textArea()

        then:
        element.control == 'Text Area'
    }

    def "should set control to dropdown"() {
        given:
        def element = new InputElement()

        when:
        element.dropdown()

        then:
        element.control == 'Dropdown'
    }

    def "should set control to checkbox"() {
        given:
        def element = new InputElement()

        when:
        element.checkbox()

        then:
        element.control == 'Checkbox'
    }

    def "should set control to radio buttons"() {
        given:
        def element = new InputElement()

        when:
        element.radioButtons()

        then:
        element.control == 'Radio Buttons'
    }

    def "should set control to calendar"() {
        given:
        def element = new InputElement()

        when:
        element.calendar()

        then:
        element.control == 'Calendar'
    }

    def "should set control to currency"() {
        given:
        def element = new InputElement()

        when:
        element.currency()

        then:
        element.control == 'Currency'
    }

    def "should set control to rich text editor"() {
        given:
        def element = new InputElement()

        when:
        element.richTextEditor()

        then:
        element.control == 'Rich Text Editor'
    }

    def "should set control to attach content"() {
        given:
        def element = new InputElement()

        when:
        element.attachContent()

        then:
        element.control == 'Attach Content'
    }

    def "should set control to auto complete"() {
        given:
        def element = new InputElement()

        when:
        element.autoComplete()

        then:
        element.control == 'AutoComplete'
    }

    def "should set control to smart prompt"() {
        given:
        def element = new InputElement()

        when:
        element.smartPrompt()

        then:
        element.control == 'Smart Prompt'
    }
    
    def "should reset properties with textInput"() {
        given:
        def element = new InputElement()
        element.required()
        element.readOnly()
        element.disabled()

        when:
        element.textInput()

        then:
        element.control == 'Text Input'
        !element.properties['required']
        !element.properties['readOnly']
        !element.properties['disabled']
    }

    def "should set required property"() {
        given:
        def element = new InputElement()

        when:
        element.required()

        then:
        element.properties['required']
    }

    def "should set readOnly property"() {
        given:
        def element = new InputElement()

        when:
        element.readOnly()

        then:
        element.properties['readOnly']
    }

    def "should set disabled property"() {
        given:
        def element = new InputElement()

        when:
        element.disabled()

        then:
        element.properties['disabled']
    }

    def "should set visibility"() {
        given:
        def element = new InputElement()

        when:
        element.visible('some_condition')

        then:
        element.visibility == 'If'
        element.condition == 'some_condition'
    }
}
