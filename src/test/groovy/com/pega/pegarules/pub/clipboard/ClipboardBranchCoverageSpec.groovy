package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class ClipboardBranchCoverageSpec extends Specification {

    def "BaseClass map constructor normalizes edge case keys"() {
        given:
        def nested = [inner: 'value']
        def payload = new LinkedHashMap()
        payload.put(null, 'null-key')
        payload.put(123L, 'numeric-key')
        payload.put('nestedPage', nested)

        when:
        def base = new BaseClass((Map) payload)

        then:
        base.getAt(null) == 'null-key'
        base.getAt('123') == 'numeric-key'
        base.getAt('nestedPage') instanceof SimpleClipboardPage
        base.getAt('nestedPage').getString('inner') == 'value'
    }

    def "Page map constructor accepts null and numeric keys"() {
        given:
        def payload = new LinkedHashMap()
        payload.put(null, 'alpha')
        payload.put(987, 'beta')

        when:
        def page = new Page((Map) payload)

        then:
        page.getAt(null) == 'alpha'
        page.getAt('987') == 'beta'
    }

    def "Page list constructor stores ClipboardProperty entries"() {
        given:
        def clipProp = Stub(ClipboardProperty) {
            getPropertyValue() >> 'prop-value'
        }

        when:
        def page = new Page([clipProp])

        then:
        page.getAt('item0') == 'prop-value'
    }

    def "Page copyFromClipboardPageSafe falls back when propertyValue is null"() {
        given:
        def backing = [beta: 'entry-beta'] as LinkedHashMap<String, Object>
        def nullProperty = Stub(ClipboardProperty) {
            getPropertyValue() >> null
        }
        def source = Stub(ClipboardPage) {
            entrySet() >> backing.entrySet()
            getProperty('beta') >> nullProperty
        }

        when:
        def page = new Page(source)

        then:
        page.getAt('beta') == 'entry-beta'
    }

    def "_toSimpleClipboardPageSafe retains null keys"() {
        given:
        def pseudoEntry = new Expando()
        pseudoEntry.getKey = { null }
        pseudoEntry.getValue = { 'null-value' }
        pseudoEntry.key = null
        pseudoEntry.value = 'null-value'
        def pseudoPage = new Object() {
            def entrySet() { [pseudoEntry] }
        }
        def page = new Page()

        when:
        def result = page._toSimpleClipboardPageSafe(pseudoPage)

        then:
        result instanceof SimpleClipboardPage
        result.keySet().contains(null)
        result.values().contains('null-value')
    }

    def "SimpleClipboardPage wrapResult handles Page inputs"() {
        given:
        def helper = SimpleClipboardPage.getDeclaredMethod('_wrapResult', Object)
        helper.setAccessible(true)
        def pageValue = new Page([alpha: 'A'])

        when:
        def wrapped = helper.invoke(new SimpleClipboardPage(), pageValue)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('alpha') == 'A'
    }

    def "SimpleClipboardPage wrapResult unwraps property maps"() {
        given:
        def helper = SimpleClipboardPage.getDeclaredMethod('_wrapResult', Object)
        helper.setAccessible(true)
        def property = Stub(ClipboardProperty) {
            getPropertyValue() >> [gamma: 'G']
        }

        when:
        def wrapped = helper.invoke(new SimpleClipboardPage(), property)

        then:
        wrapped instanceof SimpleClipboardPage
        wrapped.getAt('gamma') == 'G'
    }
}
