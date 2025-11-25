package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class JavaObjectSpec extends Specification {

    def "should create JavaObject with default constructor"() {
        when:
        def javaObject = new JavaObject()

        then:
        javaObject != null
        javaObject instanceof JavaObject
    }

    def "should create JavaObject from map"() {
        given:
        def map = ["name": "John", "age": "30"]

        when:
        def javaObject = new JavaObject(map)

        then:
        javaObject != null
        javaObject instanceof JavaObject
    }

    def "should inherit SimpleClipboardProperty methods"() {
        given:
        def javaObject = new JavaObject()

        when:
        javaObject.setValue("test")

        then:
        javaObject.getStringValue() == "test"
    }
}