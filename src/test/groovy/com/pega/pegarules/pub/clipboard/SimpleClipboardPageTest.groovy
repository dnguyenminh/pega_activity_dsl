package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPageTest extends Specification {

    def "should construct with various inputs"() {
        when:
        def p1 = new SimpleClipboardPage()
        def p2 = new SimpleClipboardPage([a: 1])
        def p3 = new SimpleClipboardPage([[b: 2]])
        def p4 = new SimpleClipboardPage(new Page([c: 3]))
        def p5 = new SimpleClipboardPage((Object) new Page([d: 4]))
        def p6 = new SimpleClipboardPage((Object) "not a page")

        then:
        p1 != null
        p2.getString('a') == '1'
        p3.getPropertyObject('b') == 2
        p4.getString('c') == '3'
        p5.getString('d') == '4'
        p6 != null
    }

    def "getAt should wrap results in SimpleClipboardPage"() {
        given:
        def p = new SimpleClipboardPage()
        p.put('map', [a: 1])
        p.put('page', new Page([b: 2]))
        p.put('scp', new SimpleClipboardPage([c: 3]))
        p.put('prop', new SimpleClipboardProperty(new Page([d: 4])))
        p.put('propMap', new SimpleClipboardProperty([e: 5]))

        expect:
        p.getAt('map') instanceof SimpleClipboardPage
        p.getAt('page') instanceof SimpleClipboardPage
        p.getAt('scp') instanceof SimpleClipboardPage
        p.getAt('prop') instanceof SimpleClipboardPage
        p.getAt('propMap') instanceof SimpleClipboardPage
        
        // Test Object key variant
        p.getAt((Object)'map') instanceof SimpleClipboardPage
    }

    def "getName should return page name"() {
        given:
        def p = new SimpleClipboardPage()
        p.rename('myPage')

        expect:
        p.getName() == 'myPage'
    }
}
