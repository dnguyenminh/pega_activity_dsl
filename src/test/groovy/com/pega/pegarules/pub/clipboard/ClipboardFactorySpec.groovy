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

    def "newPageList preserves clipboard pages and converts list descriptors"() {
        given:
        def existing = new Page([alpha: 'A'])
        def listDescriptor = [[beta: 'B']]

        when:
        def pl = ClipboardFactory.newPageList([existing, listDescriptor, 'tail'])
        def values = pl.getPropertyValue()

        then:
        values.size() == 3
        values[0].is(existing)
        values[1] instanceof SimpleClipboardPage
        values[1].getAt('beta') == 'B'
        values[2] == 'tail'
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
