package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

/**
 * Focused coverage around Page constructors that need to exercise the
 * non-Abstract ClipboardPage branches and error-handling fallbacks.
 */
class PageFallbackCoverageTest extends Specification {

    private ClipboardPage stubClipboardPage(Map<String, Object> backing, Closure behavior = null) {
        Stub(ClipboardPage) { ClipboardPage stub ->
            stub.entrySet() >> backing.entrySet()
            stub.getProperty(_ as String) >> { String key ->
                if (behavior != null) {
                    return behavior.call(key)
                }
                return new SimpleClipboardProperty(key, backing[key])
            }
        }
    }

    def "Page(Object) copies values from non-Abstract clipboard pages"() {
        given:
        def backing = [alpha: 'A', beta: 'B']
        def source = stubClipboardPage(backing)

        when:
        def page = new Page((Object) source)

        then:
        page.getString('alpha') == 'A'
        page.getString('beta') == 'B'
    }

    def "Page(Object) falls back to entry values when property retrieval fails"() {
        given:
        def backing = [alpha: 'A', beta: 'B'] as LinkedHashMap<String, Object>
        def source = Stub(ClipboardPage) { ClipboardPage stub ->
            stub.entrySet() >> backing.entrySet()
            stub.getProperty('alpha') >> new SimpleClipboardProperty('alpha', backing['alpha'])
            stub.getProperty('beta') >> { throw new IllegalStateException('boom') }
        }

        when:
        def page = new Page((Object) source)

        then:
        page.getString('alpha') == 'A'
        page.getString('beta') == 'B'
    }

    def "Page constructors unwrap list entries that include clipboard pages"() {
        given:
        def nestedBacking = [inner: 'value']
        def nestedClipboardPage = stubClipboardPage(nestedBacking)
        def payload = [[mapped: 'x'], nestedClipboardPage, 'raw-item']

        when:
        def page = new Page('listPage', payload, ClipboardPropertyType.PAGE)

        then:
        page.getString('mapped') == 'x'
        page.getString('inner') == 'value'
        page.getPropertyObject('items') instanceof List
        page.getPropertyObject('items').contains('raw-item')
        page.getName() == 'listPage'
    }

    def "Page(name,value) prefers ClipboardPage branch before Map compatibility"() {
        given:
        def backing = [alpha: 'entry-alpha']
        def source = stubClipboardPage(backing) { key ->
            new SimpleClipboardProperty(key, "prop-${key}")
        }

        when:
        def page = new Page('cpPage', source, ClipboardPropertyType.PAGE)

        then:
        page.getString('alpha') == 'prop-alpha'
    }

    def "Page(name,value) list entries honor ClipboardPage branch"() {
        given:
        def backing = [beta: 'entry-beta']
        def nested = stubClipboardPage(backing) { key ->
            new SimpleClipboardProperty(key, "prop-${key}")
        }

        when:
        def page = new Page('cpList', [nested], ClipboardPropertyType.PAGE)

        then:
        page.getString('beta') == 'prop-beta'
    }

    def "Page(ClipboardPage) uses property API for non-Abstract sources"() {
        given:
        def backing = [first: 'one', second: 'two']
        def source = stubClipboardPage(backing)

        when:
        def page = new Page(source)

        then:
        page.getString('first') == 'one'
        page.getString('second') == 'two'
    }
}
