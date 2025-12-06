package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

import groovy.util.Expando
import java.util.Set

class AbstractClipboardPageHelperSpec extends Specification {

    private static Object invokeDeep(Object value) {
        def method = AbstractClipboardPage.getDeclaredMethod('_deepUnwrapAndConvert', Object)
        method.accessible = true
        return method.invoke(null, value)
    }

    private static class InspectablePage extends SimpleClipboardPage {
        SimpleClipboardPage convert(Object input) {
            return _toSimpleClipboardPageSafe(input)
        }
    }

    private static Object invokeGetPropertyValueSafe(Object candidate) {
        def method = AbstractClipboardPage.getDeclaredMethod('_getPropertyValueSafe', Object)
        method.accessible = true
        return method.invoke(null, [candidate] as Object[])
    }

    private static boolean invokeIsClipboardProperty(Object candidate) {
        def method = AbstractClipboardPage.getDeclaredMethod('_isClipboardProperty', Object)
        method.accessible = true
        return (boolean) method.invoke(null, [candidate] as Object[])
    }

    def "deepUnwrapAndConvert normalizes nested descriptors"() {
        given:
        def property = new SimpleClipboardProperty('wrapped', [theta: 'T'])
        def reflective = new Object() {
            Object getPropertyValue() { [rho: 'R'] }
        }
        def list = [property, reflective, [sigma: 'S']]

        when:
        def converted = invokeDeep(list)

        then:
        converted instanceof List
        converted[0] instanceof Page
        converted[0].getAt('theta') == 'T'
        converted[1] instanceof Page
        converted[1].getAt('rho') == 'R'
        converted[2] instanceof Page
        converted[2].getAt('sigma') == 'S'
    }

    def "toSimpleClipboardPageSafe builds page from entry-like object"() {
        given:
        def pseudoEntry = new Expando(key: 'psi', value: 'P')
        pseudoEntry.getKey = { 'psi' }
        pseudoEntry.getValue = { 'P' }
        def pseudoPage = new Object() {
            Set entrySet() {
                [pseudoEntry] as Set
            }
        }
        def inspector = new InspectablePage()

        when:
        def converted = inspector.convert(pseudoPage)

        then:
        converted instanceof SimpleClipboardPage
        converted.getAt('psi') == 'P'
    }

    def "toSimpleClipboardPageSafe returns null when entrySet returns null"() {
        given:
        def pseudoPage = new Object() {
            Set entrySet() { null }
        }
        def inspector = new InspectablePage()

        expect:
        inspector.convert(pseudoPage) == null
    }

    def "toSimpleClipboardPageSafe falls back to key and value properties"() {
        given:
        def pseudoEntry = new Expando(key: 'phi', value: 'PHI')
        pseudoEntry.getKey = null
        pseudoEntry.getValue = null
        def pseudoPage = new Object() {
            Set entrySet() { [pseudoEntry] as Set }
        }
        def inspector = new InspectablePage()

        when:
        def converted = inspector.convert(pseudoPage)

        then:
        converted.getAt('phi') == 'PHI'
    }

    def "toSimpleClipboardPageSafe skips entries that throw during inspection"() {
        given:
        def badEntry = new Object() {
            Object getKey() { throw new IllegalStateException('bad key') }
            Object getValue() { throw new IllegalStateException('bad value') }
        }
        def goodEntry = new Expando(key: 'eta', value: 'E')
        goodEntry.getKey = { 'eta' }
        goodEntry.getValue = { 'E' }
        def pseudoPage = new Object() {
            Set entrySet() { [badEntry, goodEntry] as Set }
        }
        def inspector = new InspectablePage()

        when:
        def converted = inspector.convert(pseudoPage)

        then:
        converted.keySet().contains('eta')
        converted.getAt('eta') == 'E'
    }

    def "copy helpers unwrap nested map and property values"() {
        given:
        def source = new SimpleClipboardPage()
        source.putAt('child', [alpha: 'A'])
        source.putAt('value', new SimpleClipboardProperty('val', 7))
        source.putAt('list', [
            new SimpleClipboardPage([beta: 'B']),
            [gamma: 'G']
        ])

        when:
        def clone = source.copy()
        def dest = new SimpleClipboardPage()
        source.copyTo(dest)
        def target = new SimpleClipboardPage()
        target.copyFrom(source)

        then:
        clone.getAt('child') instanceof SimpleClipboardPage
        clone.getAt('child').getAt('alpha') == 'A'
        dest.getAt('value') == 7
        target.getAt('list') instanceof List
        target.getAt('list')[1] instanceof SimpleClipboardPage
        target.getAt('list')[1].getAt('gamma') == 'G'
    }

    def "replace removes stale entries before copying fresh content"() {
        given:
        def source = new SimpleClipboardPage([fresh: 'F'])
        def target = new SimpleClipboardPage([stale: 'S'])

        when:
        target.replace(source)

        then:
        target.getAt('fresh') == 'F'
        target.getAt('stale') == null
    }

    def "getPropertyObject wraps embedded page into clipboard property"() {
        given:
        def host = new SimpleClipboardPage()
        def nested = new SimpleClipboardPage([alpha: 'A'])
        host.putAt('nested', nested)

        when:
        def property = host.getPropertyObject('nested')

        then:
        property instanceof SimpleClipboardProperty
        property.getPageValue() instanceof Page
        property.getPageValue().getAt('alpha') == 'A'
    }

    def "_isClipboardProperty detects reflective wrappers"() {
        given:
        def reflective = new Object() {
            Object getPropertyValue() { 'value' }
        }

        expect:
        invokeIsClipboardProperty(reflective)
        !invokeIsClipboardProperty(null)
        !invokeIsClipboardProperty(new Object())
    }

    def "_getPropertyValueSafe unwraps via reflection and tolerates failures"() {
        given:
        def reflective = new Object() {
            Object getPropertyValue() { [omega: 'O'] }
        }
        def stubborn = new Object() {
            Object getPropertyValue() { throw new IllegalStateException('boom') }
        }

        expect:
        invokeGetPropertyValueSafe(reflective)['omega'] == 'O'
        invokeGetPropertyValueSafe(stubborn).is(stubborn)
        invokeGetPropertyValueSafe(null) == null
    }
}
