package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPageConstructorSpec extends Specification {

    def "map constructor normalizes keys and wraps nested structures"() {
        given:
        def payload = [
            42: 'answer',
            child: [beta: 'B'],
            (null): 'nil'
        ]

        when:
        def page = new SimpleClipboardPage((Map)payload)

        then:
        page.getAt('42') == 'answer'
        page.getAt('child') instanceof SimpleClipboardPage
        page.getAt('child').getAt('beta') == 'B'
        page.getAt(null) == 'nil'
        page.getAt('pxObjClass') == '@baseclass'
    }

    def "map constructor handles null input"() {
        expect:
        new SimpleClipboardPage((Map)null).getAt('pxObjClass') == '@baseclass'
    }

    def "map constructor accepts ClipboardProperty and ClipboardPage values"() {
        given:
        def nestedPage = new Page([gamma: 'G'])
        def wrappedProp = new SimpleClipboardProperty('payload', [delta: 'D'])

        when:
        def page = new SimpleClipboardPage((Map)[page: nestedPage, property: wrappedProp])

        then:
        page.getAt('page') instanceof SimpleClipboardPage
        page.getAt('page').getAt('gamma') == 'G'
        page.getAt('property') instanceof SimpleClipboardPage
        page.getAt('property').getAt('delta') == 'D'
    }

    def "list constructor processes mixed descriptor types"() {
        given:
        def nestedPage = new Page([gamma: 'G'])
        def property = new SimpleClipboardProperty('delta', [epsilon: 'E'])
        def descriptors = [
            [alpha: 'A'],
            nestedPage,
            property,
            'tail-item'
        ]

        when:
        def page = new SimpleClipboardPage((List)descriptors)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('gamma') == 'G'
        page.getAt('item2') instanceof SimpleClipboardPage
        page.getAt('item2').getAt('epsilon') == 'E'
        page.getAt('items') instanceof List
        page.getAt('items').contains('tail-item')
    }

    def "list constructor handles null list input"() {
        expect:
        new SimpleClipboardPage((List)null).getAt('pxObjClass') == '@baseclass'
    }

    def "clipboardPage constructor copies provided entries"() {
        given:
        def source = new Page([alpha: 'A', beta: 'B'])

        when:
        def page = new SimpleClipboardPage((ClipboardPage)source)

        then:
        page.getAt('alpha') == 'A'
        page.getAt('beta') == 'B'
    }

    def "object constructor copies clipboard pages and ignores scalars"() {
        given:
        def source = new Page([theta: 'T'])

        when:
        def viaObject = new SimpleClipboardPage((Object)source)
        def nonPage = new SimpleClipboardPage((Object)'noop')

        then:
        viaObject.getAt('theta') == 'T'
        nonPage.getAt('theta') == null
    }
}
