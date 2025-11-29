package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

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
}