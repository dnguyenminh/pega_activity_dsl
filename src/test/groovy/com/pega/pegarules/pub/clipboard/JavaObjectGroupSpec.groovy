package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class JavaObjectGroupSpec extends Specification {

    def "should construct with default constructor"() {
        when:
        def jog = new JavaObjectGroup()

        then:
        jog != null
        jog instanceof JavaObjectGroup
        jog instanceof SimpleClipboardProperty
    }

    def "should construct with map"() {
        given:
        def map = [a: new JavaObject([k: 'v1']), b: new JavaObject([k: 'v2'])]

        when:
        def jog = new JavaObjectGroup(map)
        def page = jog.getPropertyValue()

        then:
        jog != null
        page instanceof Page
        // AbstractClipboardPage unwraps properties, so we check the values
        def valA = page.getPropertyObject('a').getPropertyValue()
        valA instanceof Page
        valA.getString('k') == 'v1'

        def valB = page.getPropertyObject('b').getPropertyValue()
        valB instanceof Page
        valB.getString('k') == 'v2'
        
        jog.size() == 2
    }
}
