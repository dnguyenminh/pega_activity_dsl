package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class AbstractClipboardPageStoreValueSpec extends Specification {

    def "putAt unwraps nested ClipboardProperty payloads into SimpleClipboardPage"() {
        given:
        def nested = new SimpleClipboardProperty(new SimpleClipboardProperty([alpha: 'A']))
        def page = new SimpleClipboardPage()

        when:
        page.putAt('nested', nested)

        then:
        def stored = page.getAt('nested')
        stored instanceof SimpleClipboardPage
        stored.getAt('alpha') == 'A'
    }

    def "putAt copies SimpleClipboardPage instances to maintain isolation"() {
        given:
        def child = new SimpleClipboardPage([beta: 'B'])
        def page = new SimpleClipboardPage()

        when:
        page.putAt('child', child)

        then:
        def stored = page.@delegate['child']
        stored instanceof SimpleClipboardPage
        !stored.is(child)
        stored.getAt('beta') == 'B'
    }

    def "putAt converts ClipboardPage inputs into SimpleClipboardPage copies"() {
        given:
        def source = new Page([delta: 'D'])
        def page = new SimpleClipboardPage()

        when:
        page.putAt('copy', source)

        then:
        def stored = page.@delegate['copy']
        stored instanceof SimpleClipboardPage
        !stored.is(source)
        stored.getAt('delta') == 'D'
    }

    def "getPropertyObject wraps stored SimpleClipboardPage into ClipboardProperty"() {
        given:
        def page = new SimpleClipboardPage()
        page.@delegate['delta'] = new SimpleClipboardPage([theta: 'T'])

        when:
        def obj = page.getPropertyObject('delta')

        then:
        obj instanceof SimpleClipboardProperty
        obj.getPropertyValue() instanceof Page
        obj.getPropertyValue().getAt('theta') == 'T'
    }

    def "_toSimpleClipboardPageSafe extracts entrySet data without direct Map access"() {
        given:
        def entry = new Expando()
        entry.key = 123
        entry.value = [omega: 'W']
        def reflective = new Object() {
            def entrySet() { [entry] }
        }
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrap(page, reflective)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('123').getAt('omega') == 'W'
    }

    def "_toSimpleClipboardPageSafe still produces a page object for raw maps"() {
        given:
        def payload = [alpha: 'A']
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrap(page, payload)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('pxObjClass') == '@baseclass'
    }

    private static SimpleClipboardPage invokeWrap(SimpleClipboardPage page, Object payload) {
        def method = AbstractClipboardPage.getDeclaredMethod('_toSimpleClipboardPageSafe', Object)
        method.accessible = true
        return (SimpleClipboardPage) method.invoke(page, payload)
    }
}
