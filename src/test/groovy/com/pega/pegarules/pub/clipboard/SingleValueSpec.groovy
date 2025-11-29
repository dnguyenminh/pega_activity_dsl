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

    def "handles null and empty values"() {
        when:
        def nullValue = new SingleValue("null", null, ClipboardPropertyType.STRING.ordinal())
        def emptyValue = new SingleValue("empty", "", ClipboardPropertyType.STRING.ordinal())

        then:
        nullValue.getName() == "null"
        nullValue.getStringValue() == null
        emptyValue.getStringValue() == ""
    }

    def "supports different ClipboardPropertyType variants"() {
        when:
        def stringProp = new SingleValue("str", "value", ClipboardPropertyType.STRING.ordinal())
        def intProp = new SingleValue("num", "123", ClipboardPropertyType.INTEGER.ordinal())

        then:
        stringProp.getStringValue() == "value"
        intProp.getStringValue() == "123"
    }

    def "inherits all SimpleClipboardProperty methods"() {
        given:
        def singleValue = new SingleValue("test", "value", ClipboardPropertyType.STRING.ordinal())

        expect:
        singleValue instanceof SimpleClipboardProperty
        singleValue instanceof ClipboardProperty
        singleValue.getPropertyValue() == "value"
        singleValue.size() == 1
    }

    def "handles type conversion"() {
        when:
        def intValue = new SingleValue("num", "123", ClipboardPropertyType.INTEGER.ordinal())
        def boolValue = new SingleValue("flag", "false", ClipboardPropertyType.BOOLEAN.ordinal())

        then:
        intValue.getIntegerValue() == 123
        intValue.toInteger() == 123
        boolValue.getStringValue() == "false"
    }

    def "supports value modification"() {
        given:
        def singleValue = new SingleValue("test", "initial", ClipboardPropertyType.STRING.ordinal())

        when:
        singleValue.clearValue()

        then:
        singleValue.getPropertyValue() == null

        when:
        singleValue.setValue("new value")

        then:
        singleValue.getStringValue() == "new value"
    }
}