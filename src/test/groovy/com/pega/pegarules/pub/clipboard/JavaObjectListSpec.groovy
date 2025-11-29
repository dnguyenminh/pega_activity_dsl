package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class JavaObjectListSpec extends Specification {

    def "should construct with default constructor"() {
        when:
        def jol = new JavaObjectList()

        then:
        jol != null
        jol instanceof JavaObjectList
        jol instanceof SimpleClipboardProperty
    }

    def "should construct with list"() {
        given:
        def list = [new JavaObject([k: 'v1']), new JavaObject([k: 'v2'])]

        when:
        def jol = new JavaObjectList(list)
        def result = jol.getPropertyValue()

        then:
        jol != null
        result instanceof List
        result.size() == 2
        result[0] instanceof Page
        result[0].getString('k') == 'v1'
        result[1] instanceof Page
        result[1].getString('k') == 'v2'
        jol.size() == 2
    }
}
