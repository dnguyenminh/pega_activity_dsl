package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SingleValueSpec extends Specification {

    def "should create SingleValue with name, value and type"() {
        when:
        def singleValue = new SingleValue("testProp", "testValue", ClipboardPropertyType.STRING.ordinal())

        then:
        singleValue.getName() == "testProp"
        singleValue.getStringValue() == "testValue"
    }

    def "should inherit SimpleClipboardProperty methods"() {
        given:
        def singleValue = new SingleValue("testProp", "42", ClipboardPropertyType.INTEGER.ordinal())

        expect:
        singleValue.getIntegerValue() == 42
        singleValue.getName() == "testProp"
    }

    def "should handle different property types"() {
        when:
        def stringValue = new SingleValue("str", "hello", ClipboardPropertyType.STRING.ordinal())
        def intValue = new SingleValue("num", "123", ClipboardPropertyType.INTEGER.ordinal())
        def boolValue = new SingleValue("flag", "true", ClipboardPropertyType.BOOLEAN.ordinal())

        then:
        stringValue.getStringValue() == "hello"
        intValue.getIntegerValue() == 123
        boolValue.getStringValue() == "true" // boolean stored as string
    }

    def "should support property operations"() {
        given:
        def singleValue = new SingleValue("test", "initial", ClipboardPropertyType.STRING.ordinal())

        when:
        singleValue.setValue("updated")

        then:
        singleValue.getStringValue() == "updated"
    }
}