package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

class AbstractClipboardPageCoverageSpec extends Specification {

    def "getAt returns null for missing key and unwraps maps into Page"() {
        given:
        def page = new SimpleClipboardPage()

        expect:
        page.getAt('nope') == null

        when:
        page.putAt('m', [a:1])

        then:
        def result = page.getAt('m')
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
        page.y instanceof SimpleClipboardPage
        page.y.getPropertyObject('b') == 2
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
        dest.getPropertyObject('p') instanceof Page
    }


}