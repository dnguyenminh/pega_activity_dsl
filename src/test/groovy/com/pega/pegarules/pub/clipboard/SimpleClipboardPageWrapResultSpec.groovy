package com.pega.pegarules.pub.clipboard

import groovy.util.MapEntry
import spock.lang.Specification

class SimpleClipboardPageWrapResultSpec extends Specification {

    private static Object invokeWrap(SimpleClipboardPage page, Object value) {
        def method = SimpleClipboardPage.class.getDeclaredMethod('_wrapResult', Object)
        method.accessible = true
        return method.invoke(page, value)
    }

    def "wrapResult returns same instance for SimpleClipboardPage input"() {
        given:
        def simple = new SimpleClipboardPage([alpha: 'A'])

        when:
        def wrapped = invokeWrap(simple, simple)

        then:
        wrapped.is(simple)
    }

    def "wrapResult converts Page instances into SimpleClipboardPage"() {
        given:
        def simple = new SimpleClipboardPage()
        def child = new Page([alpha: 'A'])

        when:
        def wrapped = invokeWrap(simple, child)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('alpha') == 'A'
    }

    def "wrapResult unwraps reflective clipboard property returning Map"() {
        given:
        def simple = new SimpleClipboardPage()
        def expando = new Object() {
            Object getPropertyValue() { [beta: 'B'] }
        }

        when:
        def wrapped = invokeWrap(simple, expando)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('beta') == 'B'
    }

    def "wrapResult converts raw Map values into SimpleClipboardPage"() {
        given:
        def simple = new SimpleClipboardPage()

        when:
        def wrapped = invokeWrap(simple, [gamma: 'G'])

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('gamma') == 'G'
    }

    def "wrapResult converts non-simple ClipboardPage implementations"() {
        given:
        def simple = new SimpleClipboardPage()
        def foreign = new AbstractClipboardPage([omega: 'W']) {}

        when:
        def wrapped = invokeWrap(simple, foreign)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('omega') == 'W'
    }

    def "wrapResult unwraps ClipboardProperty containing ClipboardPage"() {
        given:
        def simple = new SimpleClipboardPage()
        def property = new SimpleClipboardProperty('delta', new Page([theta: 'T']))

        when:
        def wrapped = invokeWrap(simple, property)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('theta') == 'T'
    }

    def "wrapResult returns raw scalar values from ClipboardProperty"() {
        given:
        def simple = new SimpleClipboardPage()
        def property = new SimpleClipboardProperty('flag', 'yes')

        expect:
        invokeWrap(simple, property) == 'yes'
    }
}
