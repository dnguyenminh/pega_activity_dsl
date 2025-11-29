package com.pega.pegarules.pub.clipboard

import spock.lang.Specification
import spock.lang.Ignore

class PageGroupTest extends Specification {
    def "should set name and include page values"() {
        given:
        def a = [x: 1]
        def b = [y: 2]

        when:
        def pg = new PageGroup(['a': a, 'b': b])

        then:
        pg.getName() == 'PageGroup'
        pg.getPropertyObject('a') instanceof ClipboardProperty
        pg.getPropertyObject('a').getPageValue() instanceof ClipboardPage
        pg.getPropertyObject('b') instanceof ClipboardProperty
        pg.getPropertyObject('b').getPageValue() instanceof ClipboardPage
        pg.getPropertyObject('a').getPageValue().getString('x') == '1'
        pg.getPropertyObject('b').getPageValue().getString('y') == '2'
    }

    def "should construct with default constructor"() {
        when:
        def pg = new PageGroup()

        then:
        pg.size() >= 8
        pg.getName() == 'PageGroup'
    }

    def "should construct with list constructor"() {
        when:
        def pg = new PageGroup([])

        then:
        pg.size() >= 8
        pg.getName() == 'PageGroup'
    }

    def "should construct from map with mixed types"() {
        given:
        def page = new Page([p:1])
        def prop = new SimpleClipboardProperty('prop', 'val')
        def raw = 'rawString'

        when:
        def pg = new PageGroup([
            pageKey: page,
            propKey: prop,
            rawKey: raw,
            mapKey: [m:2]
        ])

        then:
        pg.getPropertyObject('pageKey') instanceof ClipboardProperty
        pg.getPropertyObject('pageKey').getPageValue() instanceof ClipboardPage
        pg.getPropertyObject('propKey') == 'val'
        pg.getProperty('propKey') instanceof ClipboardProperty
        pg.getPropertyObject('rawKey') == 'rawString'
        pg.getProperty('rawKey') instanceof SimpleClipboardProperty
        pg.getPropertyObject('mapKey') instanceof ClipboardProperty
        pg.getPropertyObject('mapKey').getPageValue() instanceof ClipboardPage
    }

    def "should handle null input in map constructor"() {
        when:
        def pg = new PageGroup((Map)null)

        then:
        pg.size() >= 8
    }

    def "should handle null keys in map constructor"() {
        when:
        def pg = new PageGroup([(null): [a:1]])

        then:
        pg.getAt(null) instanceof ClipboardProperty
        pg.getAt(null).getPageValue() instanceof ClipboardPage
    }
}
