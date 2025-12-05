package com.pega.pegarules.pub.clipboard

import spock.lang.Specification

/**
 * Advanced coverage tests for SimpleClipboardPage to hit uncovered branches and methods.
 */
class SimpleClipboardPageAdvancedCoverageTest extends Specification {

    def "SimpleClipboardPage Map constructor with null"() {
        when:
        def page = new SimpleClipboardPage((Map)null)

        then:
        page instanceof SimpleClipboardPage
        page != null
    }

    def "SimpleClipboardPage List constructor with null"() {
        when:
        def page = new SimpleClipboardPage((List)null)

        then:
        page instanceof SimpleClipboardPage
        page != null
    }

    def "SimpleClipboardPage ClipboardPage constructor with null"() {
        when:
        def page = new SimpleClipboardPage((ClipboardPage)null)

        then:
        page instanceof SimpleClipboardPage
        page != null
    }

    def "SimpleClipboardPage Object constructor with null"() {
        when:
        def page = new SimpleClipboardPage((Object)null)

        then:
        page instanceof SimpleClipboardPage
        page != null
    }

    def "SimpleClipboardPage Object constructor with non-ClipboardPage object"() {
        given:
        def nonPageObject = 42

        when:
        def page = new SimpleClipboardPage(nonPageObject)

        then:
        page instanceof SimpleClipboardPage
        // Should not copy anything since it's not a ClipboardPage
        page != null
    }

    def "getAt Object method covers Object parameter overload"() {
        given:
        def page = new SimpleClipboardPage()
        page.put("testKey", "testValue")

        when:
        def result = page.getAt((Object)"testKey")

        then:
        result == "testValue"
    }

    def "getAt Object method covers Object parameter with non-String key"() {
        given:
        def page = new SimpleClipboardPage()
        page.put("testKey", "testValue")

        when:
        def result = page.getAt((Object)123)

        then:
        result == null // Non-existent key
    }

    def "_wrapResult covers SimpleClipboardPage case"() {
        given:
        def page = new SimpleClipboardPage()
        def nestedPage = new SimpleClipboardPage()
        nestedPage.put("nestedKey", "nestedValue")
        page.put("pageKey", nestedPage)

        when:
        def result = page.getAt("pageKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("nestedKey") == "nestedValue"
    }

    def "_wrapResult covers Page case"() {
        given:
        def page = new SimpleClipboardPage()
        def pageInstance = new Page()
        pageInstance.put("pageKey", "pageValue")
        page.put("wrappedPage", pageInstance)

        when:
        def result = page.getAt("wrappedPage")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("pageKey") == "pageValue"
    }

    def "_wrapResult covers ClipboardPage case"() {
        given:
        def page = new SimpleClipboardPage()
        def clipboardPage = new SimpleClipboardPage()
        clipboardPage.put("clipKey", "clipValue")
        page.put("cpKey", clipboardPage)

        when:
        def result = page.getAt("cpKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("clipKey") == "clipValue"
    }

    def "_wrapResult covers Map case"() {
        given:
        def page = new SimpleClipboardPage()
        def map = [mapKey: "mapValue"]
        page.put("mapProp", map)

        when:
        def result = page.getAt("mapProp")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("mapKey") == "mapValue"
    }

    def "_wrapResult covers ClipboardProperty with ClipboardPage value"() {
        given:
        def page = new SimpleClipboardPage()
        def nestedPage = new SimpleClipboardPage()
        nestedPage.put("nested", "value")
        def property = new SimpleClipboardProperty("prop", nestedPage)
        page.put("propKey", property)

        when:
        def result = page.getAt("propKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("nested") == "value"
    }

    def "_wrapResult covers ClipboardProperty with Map value"() {
        given:
        def page = new SimpleClipboardPage()
        def map = [key: "value"]
        def property = new SimpleClipboardProperty("prop", map)
        page.put("propKey", property)

        when:
        def result = page.getAt("propKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("key") == "value"
    }

    def "_wrapResult covers ClipboardProperty with regular value"() {
        given:
        def page = new SimpleClipboardPage()
        def property = new SimpleClipboardProperty("prop", "simpleValue")
        page.put("propKey", property)

        when:
        def result = page.getAt("propKey")

        then:
        result == "simpleValue"
    }

    def "_wrapResult wraps ClipboardProperty that resolves to a Map"() {
        given:
        def page = new SimpleClipboardPage()
        def property = Stub(ClipboardProperty) {
            getPropertyValue() >> [wrapped: "mapValue"]
        }
        page.put("propKey", property)

        when:
        def result = page.getAt("propKey")

        then:
        result instanceof SimpleClipboardPage
        result.getAt("wrapped") == "mapValue"
    }

    def "_wrapResult covers exception handling in try-catch"() {
        given:
        def page = new SimpleClipboardPage()
        // Create a scenario that might cause an exception in _wrapResult
        def problematicObject = new Object() {
            @Override
            String toString() { throw new RuntimeException("Test exception") }
        }
        page.put("problematicKey", problematicObject)

        when:
        def result = page.getAt("problematicKey")

        then:
        result == problematicObject // Should return the original object when exception occurs
    }

    def "getAt covers different key types"() {
        given:
        def page = new SimpleClipboardPage()
        page.put("stringKey", "stringValue")
        page.put("numberKey", 42)

        when:
        def stringResult = page.getAt("stringKey" as Object)
        def numberResult = page.getAt("numberKey" as Object)

        then:
        stringResult == "stringValue"
        numberResult == 42
    }

    def "_wrapResult via reflection converts Page inputs"() {
        given:
        def source = new Page([alpha: 'beta'])
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrapResult(page, source)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('alpha') == 'beta'
    }

    def "_wrapResult via reflection converts arbitrary ClipboardPage"() {
        given:
        def clipboardPage = Stub(ClipboardPage)
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrapResult(page, clipboardPage)

        then:
        result instanceof SimpleClipboardPage
    }

    def "_wrapResult via reflection converts Map inputs"() {
        given:
        def map = [gamma: 'delta']
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrapResult(page, map)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('gamma') == 'delta'
    }

    def "_wrapResult unwraps ClipboardProperty returning ClipboardPage"() {
        given:
        def property = Stub(ClipboardProperty) {
            getPropertyValue() >> new Page([nested: 'value'])
        }
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrapResult(page, property)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('nested') == 'value'
    }

    def "_wrapResult unwraps ClipboardProperty returning Map"() {
        given:
        def property = Stub(ClipboardProperty) {
            getPropertyValue() >> [zeta: 'omega']
        }
        def page = new SimpleClipboardPage()

        when:
        def result = invokeWrapResult(page, property)

        then:
        result instanceof SimpleClipboardPage
        result.getAt('zeta') == 'omega'
    }

    def "_wrapResult returns original value when no branches apply"() {
        given:
        def oddObject = new Object()
        def page = new SimpleClipboardPage()

        expect:
        invokeWrapResult(page, oddObject).is(oddObject)
    }

    private static Object invokeWrapResult(SimpleClipboardPage target, Object argument) {
        def method = SimpleClipboardPage.class.getDeclaredMethod('_wrapResult', Object)
        method.setAccessible(true)
        return method.invoke(target, argument)
    }
}
