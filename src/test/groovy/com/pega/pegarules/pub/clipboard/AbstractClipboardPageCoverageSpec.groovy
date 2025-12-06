package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.util.AbstractMap
import java.util.LinkedHashSet
import java.util.Map
import java.util.Set

class AbstractClipboardPageCoverageSpec extends Specification {

    def "getAt returns null for missing key and unwraps maps into Page"() {
        given:
        println "DEBUG: SimpleClipboardPage location: ${SimpleClipboardPage.class.getProtectionDomain().getCodeSource().getLocation()}"
        def page = new SimpleClipboardPage()

        expect:
        page.getAt('nope') == null

        when:
        page.putAt('m', [a:1])

        then:
        def result = page.getAt('m')
        println "TEST: delegate['m'] = ${page.@delegate.get('m')?.getClass()?.name}"
        println "TEST: getPropertyObject('m') = ${page.getPropertyObject('m')?.getClass()?.name}"
        println "TEST: getProperty('m') = ${page.getProperty('m')?.getClass()?.name}"
        println "TEST: getAt('m') = ${result?.getClass()?.name}"
        try {
            def method = page.getClass().getMethod('getAt', [Object] as Class[])
            println "DEBUG: page.getAt declaring class = ${method.declaringClass}"
            println "DEBUG: page.class = ${page.getClass()}"
        } catch(Exception e) {
            println "DEBUG: reflection inspection failed: ${e.message}"
        }
        println "DEBUG: getAt overloads = ${page.getClass().getMethods().findAll{ it.name == 'getAt' }.collect{ it.toString() } }"
        println "DEBUG: result instanceof SimpleClipboardPage? ${result instanceof SimpleClipboardPage}"
        println "DEBUG: result instanceof ClipboardProperty? ${result instanceof ClipboardProperty}"
        println "DEBUG: delegate value class ${page.@delegate.get('m')?.getClass()}"
        result instanceof SimpleClipboardPage
        result.getPropertyObject('a') == 1
    }

    def "propertyMissing returns unwrapped values and pages"() {
        given:
        def page = new SimpleClipboardPage()
        page.put('x', 'val')

        expect:
        page.x == 'val'

        when:
        page.putAt('y', [b:2])

        then:
        // Dot access returns ClipboardProperty because of the interface definition
        page.y instanceof ClipboardProperty
        // Subscript access returns the unwrapped page
        page['y'] instanceof SimpleClipboardPage
        page['y'].getPropertyObject('b') == 2
    }

    def "list constructor copies generic ClipboardPage descriptors"() {
        given:
        def entry = new AbstractMap.SimpleEntry('fromPage', 'copied')
        def foreignPage = Stub(ClipboardPage) {
            entrySet() >> { [entry] as Set }
            getProperty(_) >> { null }
        }

        when:
        def page = new SimpleClipboardPage([foreignPage])

        then:
        page.getAt('fromPage') == 'copied'
    }

    def "replace copies from another AbstractClipboardPage and clears existing"() {
        given:
        def src = new SimpleClipboardPage()
        src.put('a', '1')
        src.putAt('p', [z:9])

        def dest = new SimpleClipboardPage()
        dest.put('old', 'x')

        when:
        dest.replace(src)

        then:
        dest.getString('old') == null
        dest.getString('a') == '1'
        dest.getPropertyObject('p') instanceof ClipboardProperty
        dest.getPropertyObject('p').getPageValue() instanceof ClipboardPage
    }

    def "putAt accepts null keys and exposes stored value"() {
        given:
        def page = new SimpleClipboardPage()

        when:
        page.putAt(null, [alpha: 7])

        then:
        page.@delegate.containsKey(null)
        def stored = page.getAt(null)
        stored instanceof SimpleClipboardPage
        stored.getPropertyObject('alpha') == 7
    }

    def "replace uses entrySet fallback for generic ClipboardPage"() {
        given:
        def dest = new SimpleClipboardPage()
        dest.put('legacy', 'stale')
        def entries = [
            new AbstractMap.SimpleEntry('fresh', 'value'),
            new AbstractMap.SimpleEntry('mapProp', [inner: 5])
        ] as LinkedHashSet
        def stub = buildEntrySetOnlyClipboardPage(entries)

        when:
        dest.replace(stub)

        then:
        dest.getString('legacy') == null
        dest.getString('fresh') == 'value'
        dest.getPropertyObject('mapProp') instanceof ClipboardProperty
        dest.getAt('mapProp').getPropertyObject('inner') == 5
    }

    def "replace ignores null sources"() {
        given:
        def page = new SimpleClipboardPage([alpha: 'A'])

        when:
        page.replace(null)

        then:
        page.isEmpty()
    }

    def "_storeValue clones ClipboardPage inputs to avoid aliasing"() {
        given:
        def original = new SimpleClipboardPage()
        original.put('token', 'alpha')
        def page = new SimpleClipboardPage()

        when:
        page.putAt('copy', original)
        original.put('token', 'beta')

        then:
        def stored = page.@delegate.get('copy')
        stored instanceof SimpleClipboardPage
        !stored.is(original)
        page.getAt('copy').getString('token') == 'alpha'
    }

    def "_storeValue unwraps stubborn ClipboardProperty via fallback path"() {
        given:
        def originalIsClipboardProperty = AbstractClipboardPage.metaClass.getMetaMethod('_isClipboardProperty', [Object] as Class[])
        AbstractClipboardPage.metaClass.'static'._isClipboardProperty = { Object candidate ->
            if (candidate instanceof ToggleClipboardProperty) {
                candidate.classificationChecks++
                return candidate.classificationChecks != 2
            }
            originalIsClipboardProperty.invoke(AbstractClipboardPage, [candidate] as Object[])
        }
        def page = new Page()
        def property = new ToggleClipboardProperty()

        when:
        page.putAt('fallback', property)

        then:
        page.getString('fallback') == 'final-payload'

        cleanup:
        GroovySystem.metaClassRegistry.removeMetaClass(AbstractClipboardPage)
    }

    def "containsValue inspects property and raw delegates"() {
        given:
        def page = new SimpleClipboardPage()
        page.put('wrapped', 'needle')
        page.@delegate.put('raw', 42)

        expect:
        page.containsValue('needle')
        page.containsValue(42)
        !page.containsValue('missing')
    }

    def "get returns String form when delegate stores raw object"() {
        given:
        def page = new SimpleClipboardPage()
        page.@delegate.put('raw', 123)

        expect:
        page.get('raw') == '123'
    }

    def "put returns previous String when prior value bypassed property wrapping"() {
        given:
        def page = new SimpleClipboardPage()
        page.@delegate.put('existing', 3.14)

        when:
        def previous = page.put('existing', 'updated')

        then:
        previous == '3.14'
        page.getString('existing') == 'updated'
    }

    def "putAll handles null map and preserves null keys"() {
        given:
        def page = new SimpleClipboardPage()

        when:
        page.putAll((Map) null)
        page.putAll([(null): 'nullKey', named: 'value'])

        then:
        def storedNull = page.@delegate.get(null)
        storedNull instanceof ClipboardProperty
        storedNull.getStringValue() == 'nullKey'
        page.getString('named') == 'value'
    }

    def "getAt normalizes Page, ClipboardPage, and Map payloads"() {
        given:
        def harness = new GetAtTestPage()

        when:
        harness.payload = new Page([alpha: '1'])
        def fromPage = harness.getAt('alphaPage')

        then:
        fromPage instanceof SimpleClipboardPage
        fromPage.getString('alpha') == '1'

        when:
        def customClipboard = new HarnessClipboardPage([beta: '2'])
        harness.payload = customClipboard
        def fromClipboard = harness.getAt('betaPage')

        then:
        fromClipboard instanceof SimpleClipboardPage
        fromClipboard.getString('beta') == '2'

        when:
        harness.payload = [gamma: 3]
        def fromMap = harness.getAt('gammaPage')

        then:
        fromMap instanceof SimpleClipboardPage
        fromMap.getString('gamma') == '3'
    }

    def "getAt converts list members consistently"() {
        given:
        def harness = new GetAtTestPage()
        def items = [
            new SimpleClipboardPage([one: '1']),
            new Page([two: '2']),
            new HarnessClipboardPage([nested: 'value']),
            [three: '3'],
            'scalar'
        ]
        harness.payload = items

        when:
        def result = harness.getAt('listPayload')

        then:
        result[0].getString('one') == '1'
        result[1].getString('two') == '2'
        result[2].getString('nested') == 'value'
        result[3].getString('three') == '3'
        result[4] == 'scalar'
    }

    private static class ToggleClipboardProperty extends SimpleClipboardProperty {
        int classificationChecks = 0
        private int valueCalls = 0

        ToggleClipboardProperty() {
            super('seed')
        }

        @Override
        Object getPropertyValue() {
            valueCalls++
            return valueCalls == 1 ? this : 'final-payload'
        }
    }

    private static ClipboardPage buildEntrySetOnlyClipboardPage(Set<Map.Entry> entries) {
        InvocationHandler handler = { proxy, method, args ->
            if (method.name == 'entrySet') {
                return entries
            }
            // Provide benign defaults for methods that are never exercised in this test.
            if (method.returnType == Boolean.TYPE) return false
            if (method.returnType == Integer.TYPE || method.returnType == Long.TYPE) return 0
            return null
        }
        return Proxy.newProxyInstance(
            ClipboardPage.class.classLoader,
            [ClipboardPage] as Class[],
            handler
        ) as ClipboardPage
    }

    def "getPropertyObject unwraps fallback ClipboardProperty layers"() {
        given:
        def inner = new SimpleClipboardProperty('leaf')
        def outer = new SimpleClipboardProperty(inner)
        def page = new TestBypassingUnwrapPage(outer)
        page.@delegate.put('nested', outer)

        when:
        def value = page.getPropertyObject('nested')

        then:
        value == 'leaf'
    }

    private static class TestBypassingUnwrapPage extends Page {
        private final ClipboardProperty forced

        TestBypassingUnwrapPage(ClipboardProperty forced) {
            super()
            this.forced = forced
        }

        @Override
        protected Object _unwrapPropertyValue(Object candidate) {
            if (candidate.is(forced)) {
                return forced // Skip default unwrap to force getPropertyObject fallback loop
            }
            return super._unwrapPropertyValue(candidate)
        }
    }

    private static class GetAtTestPage extends SimpleClipboardPage {
        Object payload

        @Override
        Object getPropertyObject(String aReference) {
            return payload
        }
    }

    private static class HarnessClipboardPage extends AbstractClipboardPage {
        HarnessClipboardPage() {
            super()
        }

        HarnessClipboardPage(Map m) {
            super(m)
        }
    }
}