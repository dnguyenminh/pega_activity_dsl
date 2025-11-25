package com.pega.dsl

import spock.lang.Specification

class PropertyBuilderSpec extends Specification {

    def "builder sets types, modes and attributes"() {
        given:
        def p = new Property()
        def b = new PropertyBuilder(p)

        expect:
        b.doCall() is b

        when:
        b.description('desc').text(10).integer().decimal().date().dateTime().timeOfDay()
        b.trueFalse().identifier().single().page().pageList().pageGroup().valueList().valueGroup().javaObject()
        b.required(true).defaultValue('X').validValues(['a','b']).validation('rule1')

        then:
        p.description == 'desc'
    // final type should reflect the last type-setting call (identifier())
    p.propertyType == 'Identifier'
    // javaObject() sets the mode to 'Java Object'
    p.mode == 'Java Object'
        p.isRequired == true
        p.defaultValue == 'X'
        p.validValues == ['a','b']
        p.validationRule == 'rule1'
    }

    def "methodMissing call handling returns builder and non-string throws"() {
        given:
        def p = new Property()
        def b = new PropertyBuilder(p)

        expect:
        b.methodMissing('call', ['hello'] as Object[]) is b

        when:
        b.methodMissing('call', [1] as Object[])

        then:
        thrown(MissingMethodException)
    }
}
