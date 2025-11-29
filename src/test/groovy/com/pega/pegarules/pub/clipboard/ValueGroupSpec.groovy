package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ValueGroupSpec extends Specification {

    def "should construct with default constructor"() {
        when:
        def vg = new ValueGroup()

        then:
        vg != null
        vg instanceof ValueGroup
        vg instanceof SimpleClipboardProperty
    }

    def "should construct with map"() {
        given:
        def map = [a: 1, b: 2]

        when:
        def vg = new ValueGroup(map)

        then:
        vg != null
        vg.getPropertyValue() instanceof Page
        vg.getPropertyValue().get('a') == '1'
        vg.getPropertyValue().get('b') == '2'
        vg.size() == 2
    }
}
