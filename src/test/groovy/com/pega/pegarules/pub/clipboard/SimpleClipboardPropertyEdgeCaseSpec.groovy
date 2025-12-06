package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPropertyEdgeCaseSpec extends Specification {

    def "add at index promotes existing scalar to list"() {
        given:
        def property = new SimpleClipboardProperty('seed')

        when:
        property.add(0, 'first')

        then:
        property.value instanceof List
        property.value == ['first', 'seed']
    }

    def "add at index initializes empty list when property is null"() {
        given:
        def property = new SimpleClipboardProperty(null)

        when:
        property.add(0, 'only')

        then:
        property.value == ['only']
    }

    def "get(int) wraps map and clipboard pages"() {
        given:
        def nestedMap = [alpha: 'A']
        def nestedPage = new SimpleClipboardPage([beta: 'B'])
        def property = new SimpleClipboardProperty([nestedMap, nestedPage])

        when:
        def first = property.get(0)
        def second = property.get(1)

        then:
        first.getPropertyValue() instanceof Page
        first.getPropertyValue().getAt('alpha') == 'A'
        second.getPropertyValue() instanceof Page
        second.getPropertyValue().getAt('beta') == 'B'
    }

    def "get(String) wraps maps and clipboard pages"() {
        given:
        def property = new SimpleClipboardProperty([
            mapEntry: [gamma: 'G'],
            pageEntry: new SimpleClipboardPage([theta: 'T'])
        ])

        expect:
        property.get('mapEntry').getPropertyValue() instanceof Page
        property.get('mapEntry').getPropertyValue().getAt('gamma') == 'G'
        property.get('pageEntry').getPropertyValue() instanceof Page
        property.get('pageEntry').getPropertyValue().getAt('theta') == 'T'
    }

    def "get accessors return null when backing type is mismatched"() {
        expect:
        new SimpleClipboardProperty('scalar').get(0) == null
        new SimpleClipboardProperty('scalar').get('key') == null
    }

    def "numeric coercers handle invalid content"() {
        given:
        def property = new SimpleClipboardProperty('not-a-number')

        expect:
        property.getBigDecimalValue() == null
        property.getDoubleValue() == 0.0d
        property.getIntegerValue() == 0
    }

    def "getType identifies double and iterator wraps map values"() {
        given:
        def property = new SimpleClipboardProperty(3.14d)
        def mapProperty = new SimpleClipboardProperty([first: [delta: 'D']])

        when:
        def iterator = mapProperty.iterator()
        def page = iterator.next()

        then:
        property.getType() == ClipboardProperty.TYPE_DOUBLE
        page instanceof Page
        page.getAt('delta') == 'D'
    }
}
