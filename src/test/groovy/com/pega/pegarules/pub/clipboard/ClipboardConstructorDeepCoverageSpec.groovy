package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ClipboardConstructorDeepCoverageSpec extends Specification {

    def "Page list constructor handles mixed descriptors"() {
        given:
        def nestedPage = new Page([beta: 'B'])
        def property = new SimpleClipboardProperty('gamma', 'G')
        def rawValue = 99
        def descriptors = [
            [alpha: 'A'],
            nestedPage,
            property,
            rawValue
        ]

        when:
        def page = new Page((List) descriptors)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('beta') == 'B'
        page.getAt('item2') == 'G'
        page.getAt('items') instanceof List
        page.getAt('items').contains(rawValue)
    }

    def "Page map constructor normalizes keys and wraps clipboard structures"() {
        given:
        def childPage = new Page([theta: 'T'])
        def property = new SimpleClipboardProperty('prop', [phi: 'P'])
        def payload = new LinkedHashMap()
        payload.put(123L, 'numeric')
        payload.put('child', childPage)
        payload.put('property', property)

        when:
        def page = new Page((Map) payload)

        then:
        page.getAt('123') == 'numeric'
        page.getAt('child') instanceof SimpleClipboardPage
        page.getAt('child').getAt('theta') == 'T'
        page.getAt('property') instanceof SimpleClipboardPage
        page.getAt('property').getAt('phi') == 'P'
    }

    def "SimpleClipboardPage list constructor converts clipboard property and raw values"() {
        given:
        def nested = new SimpleClipboardPage([delta: 'D'])
        def property = new SimpleClipboardProperty('epsilon', [eps: 'E'])
        def descriptors = [
            [alpha: 'A'],
            nested,
            property,
            'tail'
        ]

        when:
        def page = new SimpleClipboardPage((List) descriptors)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('delta') == 'D'
        def propertyBucket = page.getAt('item2')
        propertyBucket instanceof SimpleClipboardPage
        propertyBucket.getAt('eps') == 'E'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail')
    }

    def "SimpleClipboardPage map constructor unwraps clipboard properties"() {
        given:
        def property = new SimpleClipboardProperty('wrapped', [omega: 'O'])
        def payload = new LinkedHashMap()
        payload.put(null, 'nil-value')
        payload.put('wrapped', property)

        when:
        def page = new SimpleClipboardPage((Map) payload)

        then:
        page.getAt((Object) null) == 'nil-value'
        def wrapped = page.getAt('wrapped')
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('omega') == 'O'
    }

    def "Page putAt unwraps expando-based property via reflection"() {
        given:
        def expandoProperty = new Object() {
            def getPropertyValue() { [alpha: 'A'] }
        }
        def page = new Page()

        when:
        page.putAt('expando', expandoProperty)

        then:
        def stored = page.getAt('expando')
        stored instanceof SimpleClipboardPage
        stored.getAt('alpha') == 'A'
    }

    def "Page putAt unwraps nested property layers"() {
        given:
        def doubleWrapped = new Object() {
            def getPropertyValue() {
                new SimpleClipboardProperty('inner', [kappa: 'K'])
            }
        }
        def page = new Page()

        when:
        page.putAt('double', doubleWrapped)

        then:
        def stored = page.getAt('double')
        stored instanceof SimpleClipboardPage
        stored.getAt('kappa') == 'K'
    }
}
