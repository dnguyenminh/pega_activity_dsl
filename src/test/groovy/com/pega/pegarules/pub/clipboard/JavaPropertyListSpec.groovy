package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class JavaPropertyListSpec extends Specification {

    def "should construct with default constructor"() {
        when:
        def jpl = new JavaPropertyList()

        then:
        jpl != null
        jpl instanceof JavaPropertyList
        jpl instanceof SimpleClipboardProperty
    }

    def "should construct with list"() {
        given:
        def list = [new JavaProperty('v1'), new JavaProperty('v2')]

        when:
        def jpl = new JavaPropertyList(list)

        then:
        jpl != null
        jpl.getPropertyValue() == ['v1', 'v2']
        jpl.size() == 2
    }
}
