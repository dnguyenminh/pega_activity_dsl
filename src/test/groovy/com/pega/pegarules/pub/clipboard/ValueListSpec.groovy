package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ValueListSpec extends Specification {

    def "should construct with default constructor"() {
        when:
        def vl = new ValueList()

        then:
        vl != null
        vl instanceof ValueList
        vl instanceof SimpleClipboardProperty
    }

    def "should construct with list"() {
        given:
        def list = [1, 2, 3]

        when:
        def vl = new ValueList(list)

        then:
        vl != null
        vl.getPropertyValue() == list
        vl.size() == 3
    }
}
