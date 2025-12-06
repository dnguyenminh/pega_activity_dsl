package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class SimpleClipboardPropertyValueSpec extends Specification {

    def "getPropertyValue returns stored Page instance"() {
        given:
        def page = new Page([foo: 'bar'])
        def property = new SimpleClipboardProperty(page)

        when:
        def result = property.getPropertyValue()

        then:
        result.is(page)
        result.getString('foo') == 'bar'
    }

    def "getPropertyValue unwraps nested ClipboardProperty values"() {
        given:
        def nested = new SimpleClipboardProperty([alpha: 'A'])
        def property = new SimpleClipboardProperty(nested)

        when:
        def result = property.getPropertyValue()

        then:
        result instanceof Page
        result.getString('alpha') == 'A'
    }

    def "list payloads normalize maps pages clipboard properties and scalars"() {
        given:
        def existingPage = new Page([p: 'page'])
        def clipboardPage = new SimpleClipboardPage([c: 'clip'])
        def mapValue = [m: 'map']
        def nestedProperty = new SimpleClipboardProperty([n: 'nested'])
        def rawScalar = 42
        def property = new SimpleClipboardProperty([
            existingPage,
            clipboardPage,
            mapValue,
            nestedProperty,
            rawScalar
        ])

        when:
        def results = property.getPropertyValue()

        then:
        results.size() == 5
        results[0].is(existingPage)
        results[0].getString('p') == 'page'
        results[1] instanceof Page
        results[1].getString('c') == 'clip'
        results[2] instanceof Page
        results[2].getString('m') == 'map'
        results[3] instanceof Page
        results[3].getString('n') == 'nested'
        results[4] == rawScalar
    }
}
