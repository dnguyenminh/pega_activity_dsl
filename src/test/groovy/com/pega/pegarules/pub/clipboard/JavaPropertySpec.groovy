package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class JavaPropertySpec extends Specification {

    def "default constructor creates JavaProperty"() {
        when:
        def prop = new JavaProperty()

        then:
        prop != null
        prop instanceof JavaProperty
        prop instanceof SimpleClipboardProperty
    }

    def "constructor with value creates JavaProperty with correct value"() {
        when:
        def prop = new JavaProperty("test value")

        then:
        prop != null
        prop instanceof JavaProperty
        prop.getPropertyValue() == "test value"
    }

    def "constructor with name and value creates JavaProperty with correct attributes"() {
        when:
        def prop = new JavaProperty("testName", 42)

        then:
        prop != null
        prop instanceof JavaProperty
        prop.getPropertyValue() == 42
        prop.getName() == "testName"
    }

    def "inherits SimpleClipboardProperty methods"() {
        given:
        def prop = new JavaProperty("test", "value")

        expect:
        prop.getPropertyValue() == "value"
        prop.getName() == "test"
        prop.getStringValue() == "value"
    }

    def "handles null values correctly"() {
        when:
        def prop1 = new JavaProperty(null)
        def prop2 = new JavaProperty("name", null)

        then:
        prop1 != null
        prop1.getPropertyValue() == null
        prop2.getName() == "name"
        prop2.getPropertyValue() == null
    }

    def "extends SimpleClipboardProperty correctly"() {
        given:
        def prop = new JavaProperty("test", [key: "value"])

        expect:
        prop instanceof SimpleClipboardProperty
        prop instanceof ClipboardProperty
        prop.getName() == "test"
    }

    def "handles different value types"() {
        when:
        def stringProp = new JavaProperty("str", "text")
        def intProp = new JavaProperty("num", 42)
        def mapProp = new JavaProperty("map", [a: 1, b: 2])
        def listProp = new JavaProperty("list", [1, 2, 3])

        then:
        stringProp.getPropertyValue() == "text"
        intProp.getPropertyValue() == 42
        mapProp.getPropertyValue() instanceof Map
        listProp.getPropertyValue() instanceof List
    }
}