package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class PageDirectConstructorSpec extends Specification {

    def "explicit map constructor copies content and wraps nested maps"() {
        given:
        def ctor = Page.getDeclaredConstructor(Map)
        ctor.accessible = true
        def payload = [alpha: 'A', child: [beta: 'B']]

        when:
        def page = ctor.newInstance(payload)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('child') instanceof SimpleClipboardPage
        page.getAt('child').getAt('beta') == 'B'
        page.getAt('pxObjClass') == '@baseclass'
    }

    def "explicit map constructor unwraps clipboard properties and simple pages"() {
        given:
        def ctor = Page.getDeclaredConstructor(Map)
        ctor.accessible = true
        def nestedProperty = new SimpleClipboardProperty(new SimpleClipboardProperty(new Page([gamma: 'G'])))
        def simplePage = new SimpleClipboardPage([delta: 'D'])
        def payload = [fromProperty: nestedProperty, fromSimplePage: simplePage]

        when:
        def page = ctor.newInstance(payload)

        then:
        page.getAt('fromProperty') instanceof SimpleClipboardPage
        page.getAt('fromProperty').getAt('gamma') == 'G'
        page.getAt('fromSimplePage') instanceof SimpleClipboardPage
        page.getAt('fromSimplePage').getAt('delta') == 'D'
    }

    def "explicit list constructor processes descriptors and scalars"() {
        given:
        def ctor = Page.getDeclaredConstructor(List)
        ctor.accessible = true
        def descriptors = [
            [gamma: 'G'],
            new SimpleClipboardProperty([delta: 'D']),
            'tail-value'
        ]

        when:
        def page = ctor.newInstance(descriptors)

        then:
        page.getAt('gamma') == 'G'
        page.getAt('item1') instanceof SimpleClipboardPage
        page.getAt('item1').getAt('delta') == 'D'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail-value')
    }

    def "explicit list constructor copies clipboard pages and wraps nested lists"() {
        given:
        def ctor = Page.getDeclaredConstructor(List)
        ctor.accessible = true
        def nestedPage = new SimpleClipboardPage([epsilon: 'E'])
        def nestedList = [['zeta': 'Z'], 'tail']
        def descriptors = [nestedPage, nestedList]

        when:
        def page = ctor.newInstance(descriptors)

        then:
        page.getAt('epsilon') == 'E'
        page.getAt('items') instanceof List
        page.getAt('items').any { it instanceof List }
    }
}
