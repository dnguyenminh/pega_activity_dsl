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
}