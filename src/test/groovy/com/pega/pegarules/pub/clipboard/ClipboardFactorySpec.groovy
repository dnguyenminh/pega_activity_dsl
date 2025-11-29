package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ClipboardFactorySpec extends Specification {

    def "newPage(String) sets pxObjClass"() {
        when:
        def p = ClipboardFactory.newPage('MyClass')

        then:
        p.getPropertyObject('pxObjClass') == 'MyClass'
    }

    def "newPage(Map,pxObjClass) sets pxObjClass and copies map"() {
        when:
        def p = ClipboardFactory.newPage([x:1], 'MyMapClass')

        then:
        p.getPropertyObject('pxObjClass') == 'MyMapClass'
        p.getPropertyObject('x') == 1
    }

    def "newPage(List,pxObjClass) converts list of maps to page and sets pxObjClass"() {
        when:
        def p = ClipboardFactory.newPage([[a:1],[b:2]], 'L')

        then:
        p.getPropertyObject('pxObjClass') == 'L'
        p.getPropertyObject('a') == 1
    }

    def "newPageList converts maps to SimpleClipboardPage and returns PageList"() {
        when:
        def pl = ClipboardFactory.newPageList([[a:1], [b:2]])

        then:
        pl instanceof PageList
        pl.getPropertyValue().size() == 2
        pl.getPropertyValue()[0] instanceof SimpleClipboardPage
    }

    def "newPageList handles null input"() {
        when:
        def pl = ClipboardFactory.newPageList(null)

        then:
        pl instanceof PageList
        pl.getPropertyValue().isEmpty()
    }

    def "newProperty returns SimpleClipboardProperty with name and type"() {
        when:
        def prop = ClipboardFactory.newProperty('amt', 5)

        then:
        prop instanceof SimpleClipboardProperty
        prop.getPropertyValue() == 5
        prop.getName() == 'amt'
    }
}
