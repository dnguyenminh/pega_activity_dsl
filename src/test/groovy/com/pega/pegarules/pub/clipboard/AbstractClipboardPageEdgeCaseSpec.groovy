package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class AbstractClipboardPageEdgeCaseSpec extends Specification {

    private static Object invokeUnwrap(SimpleClipboardPage page, Object payload) {
        def method = AbstractClipboardPage.getDeclaredMethod('_unwrapPropertyValue', Object)
        method.accessible = true
        return method.invoke(page, payload)
    }

    private static boolean invokeIsListOfPageInstances(List payload) {
        def method = AbstractClipboardPage.getDeclaredMethod('isListOfPageInstances', List)
        method.accessible = true
        return (boolean) method.invoke(null, payload)
    }

    def "put handles null keys and raw delegates"() {
        given:
        def page = new SimpleClipboardPage()
        page.@delegate[null] = new SimpleClipboardProperty('first', 'one')
        page.@delegate['legacy'] = 'raw-value'

        when:
        def prevNull = page.put(null, 'two')
        def prevRaw = page.put('legacy', 'next')

        then:
        prevNull == 'one'
        prevRaw == 'raw-value'
        page.getString(null) == 'two'
        page.getString('legacy') == 'next'
    }

    def "getString returns clipboard property and raw fallbacks"() {
        given:
        def page = new SimpleClipboardPage()
        page.putAt('prop', 'value')
        page.@delegate['raw'] = 42

        expect:
        page.getString('prop') == 'value'
        page.getString('raw') == '42'
        page.getString('missing') == null
    }

    def "copyTo ignores destinations that are not AbstractClipboardPage"() {
        given:
        def source = new SimpleClipboardPage([alpha: 'A'])
        def dest = Stub(ClipboardPage)

        when:
        source.copyTo(dest)

        then:
        source.getAt('alpha') == 'A'
        noExceptionThrown()
    }

    def "_unwrapPropertyValue retains Page instances"() {
        given:
        def probe = new SimpleClipboardPage()
        def payload = new Page([beta: 'B'])

        expect:
        invokeUnwrap(probe, payload).is(payload)
    }

    def "getBigDecimal returns null for non numeric values"() {
        given:
        def page = new SimpleClipboardPage([amount: 'abc'])

        expect:
        page.getBigDecimal('amount') == null
    }

    def "isListOfPageInstances rejects mixed content"() {
        given:
        def list = [new Page([delta: 'D']), 'not-a-page']

        expect:
        !invokeIsListOfPageInstances(list)
    }

    def "list constructor copies embedded clipboard pages"() {
        given:
        def embedded = new SimpleClipboardPage([gamma: 'G'])

        when:
        def page = new SimpleClipboardPage([embedded])

        then:
        page.getAt('gamma') == 'G'
    }
}
