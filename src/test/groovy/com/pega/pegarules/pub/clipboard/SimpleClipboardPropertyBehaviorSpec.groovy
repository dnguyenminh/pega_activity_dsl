package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPropertyBehaviorSpec extends Specification {

    def "add operations promote values to list form"() {
        when:
        def prop = new SimpleClipboardProperty()
        prop.add('first')
        prop.add(0, 'zeroth')
        prop.add([alpha: 'A'])

        then:
        prop.value instanceof List
        prop.value[0] == 'zeroth'
        prop.value[1] == 'first'
        prop.value[2] instanceof Map
        prop.value[2]['alpha'] == 'A'
    }

    def "get index and key return wrapped clipboard properties"() {
        given:
        def prop = new SimpleClipboardProperty()
        prop.setValue([
            [beta: 'B'],
            new SimpleClipboardPage([gamma: 'G'])
        ])

        expect:
        def first = prop.get(0).getPropertyValue()
        first instanceof Page
        first.get('beta') == 'B'
        def mapBacked = new SimpleClipboardProperty(([theta: 'T']) as Object)
        mapBacked.get('theta').getPropertyValue() == 'T'
    }

    def "iterator emits Page instances for list and map values"() {
        given:
        def listProp = new SimpleClipboardProperty([
            [delta: 'D'],
            [epsilon: 'E']
        ])
        def mapProp = new SimpleClipboardProperty(([zeta: [eta: 'H']]) as Object)

        when:
        def listResults = listProp.iterator().collect { it.getAt('delta') ?: it.getAt('epsilon') }
        def mapResults = mapProp.iterator().collect { it.getAt('eta') }

        then:
        listResults.contains('D')
        listResults.contains('E')
        mapResults == ['H']
    }

    def "contains and remove handle list and map payloads"() {
        given:
        def prop = new SimpleClipboardProperty(['apple', 'banana'])
        def mapProp = new SimpleClipboardProperty(([fruit: 'pear']) as Object)

        expect:
        prop.contains('banana')
        mapProp.contains('pear')

        when:
        prop.remove(0)
        mapProp.remove('fruit')

        then:
        prop.value == ['banana']
        mapProp.value == [:]
    }

    def "equals and hashCode compare underlying property values"() {
        given:
        def propA = new SimpleClipboardProperty('value')
        def propB = new SimpleClipboardProperty('value')
        def different = new SimpleClipboardProperty('other')
        def pageProp = new SimpleClipboardProperty(new SimpleClipboardPage([omega: 'O']))

        expect:
        propA == propB
        propA.hashCode() == propB.hashCode()
        propA == 'value'
        propA != different
        pageProp != propA
    }
}
