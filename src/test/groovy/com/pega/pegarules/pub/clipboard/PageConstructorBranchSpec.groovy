package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

import java.util.AbstractMap

class PageConstructorBranchSpec extends Specification {

    def "Page(name, list, type) copies descriptors and raw values"() {
        given:
        def nestedPage = new SimpleClipboardPage([alpha: 'A'])
        def descriptors = [
            nestedPage,
            [beta: 'B'],
            'tail-value'
        ]

        when:
        def page = new Page('NamedPage', descriptors, ClipboardPropertyType.PAGE)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('beta') == 'B'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail-value')
        page.getName() == 'NamedPage'
    }

    def "Page(name, object, type) copies ClipboardPage payloads"() {
        given:
        def source = new SimpleClipboardPage([gamma: 'G'])

        when:
        def page = new Page('ClipboardInbound', source, ClipboardPropertyType.PAGE)

        then:
        page.getAt('gamma') == 'G'
        page.getName() == 'ClipboardInbound'
    }

    def "Page(name, object, type) overlays Map payloads"() {
        given:
        def payload = [delta: 'D', nested: [epsilon: 'E']]

        when:
        def page = new Page('MapInbound', payload, ClipboardPropertyType.PAGE)

        then:
        page.getAt('delta') == 'D'
        page.getAt('nested') instanceof SimpleClipboardPage
        page.getAt('nested').getAt('epsilon') == 'E'
        page.getName() == 'MapInbound'
    }

    def "Page(Object) copies from AbstractClipboardPage and Map payloads"() {
        given:
        def sourcePage = new SimpleClipboardPage([gamma: 'G'])
        def backingMap = [delta: 'D']

        when:
        def fromPage = new Page(sourcePage)
        def fromMap = new Page(backingMap)

        then:
        fromPage.getAt('gamma') == 'G'
        fromMap.getAt('delta') == 'D'
    }

    def "Page(ClipboardPage) uses copyFromClipboardPageSafe for non-Abstract sources"() {
        given:
        def propertyPage = new SimpleClipboardPage([theta: 'T'])
        def property = new SimpleClipboardProperty('propKey', propertyPage)
        def entries = [
            new AbstractMap.SimpleEntry('propKey', 'ignored'),
            new AbstractMap.SimpleEntry('fallbackKey', 'fallback')
        ] as Set

        def stub = Stub(ClipboardPage) {
            entrySet() >> entries
            getProperty('propKey') >> property
            getProperty('fallbackKey') >> { throw new IllegalStateException('boom') }
            getProperty(_) >> null
        }

        when:
        def page = new Page(stub)

        then:
        page.getAt('propKey') instanceof SimpleClipboardPage
        page.getAt('propKey').getAt('theta') == 'T'
        page.getAt('fallbackKey') == 'fallback'
    }

    def "Page(String, ClipboardPage) copies entry values directly"() {
        given:
        def source = new SimpleClipboardPage([omega: 'O'])

        when:
        def page = new Page('Outbound', source)

        then:
        page.getAt('omega') == 'O'
        page.getName() == 'Outbound'
    }
}
